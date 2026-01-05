#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Default values
FORCE=false

# Parse command line arguments
while [[ $# -gt 0 ]]; do
  case $1 in
    -f|--force)
      FORCE=true
      shift
      ;;
    -h|--help)
      echo "Usage: ./release.sh [OPTIONS]"
      echo ""
      echo "Options:"
      echo "  -f, --force    Force overwrite existing local and remote tag"
      echo "  -h, --help     Show this help message"
      echo ""
      echo "This script will:"
      echo "  1. Extract version from build.gradle"
      echo "  2. Create a git tag (v<version>)"
      echo "  3. Push the tag to remote repository"
      echo "  4. Trigger GitHub Actions to build and release"
      exit 0
      ;;
    *)
      echo -e "${RED}Error: Unknown option $1${NC}"
      echo "Use --help for usage information"
      exit 1
      ;;
  esac
done

# Check if we're in a git repository
if ! git rev-parse --is-inside-work-tree > /dev/null 2>&1; then
  echo -e "${RED}Error: Not a git repository${NC}"
  exit 1
fi

# Check if build.gradle exists
if [ ! -f "build.gradle" ]; then
  echo -e "${RED}Error: build.gradle not found${NC}"
  exit 1
fi

# Extract version from build.gradle
VERSION=$(grep "^version = " build.gradle | sed "s/version = ['\"]*\\([^'\"]*\\)['\"]*$/\\1/" | xargs)

if [ -z "$VERSION" ]; then
  echo -e "${RED}Error: Could not extract version from build.gradle${NC}"
  exit 1
fi

TAG="v${VERSION}"

echo -e "${GREEN}=== CubisLang Release Script ===${NC}"
echo -e "Version: ${YELLOW}${VERSION}${NC}"
echo -e "Tag: ${YELLOW}${TAG}${NC}"
echo ""

# Check if tag already exists locally
if git rev-parse "$TAG" >/dev/null 2>&1; then
  if [ "$FORCE" = true ]; then
    echo -e "${YELLOW}Tag ${TAG} already exists locally. Deleting...${NC}"
    git tag -d "$TAG"
  else
    echo -e "${RED}Error: Tag ${TAG} already exists locally${NC}"
    echo -e "Use ${YELLOW}--force${NC} flag to overwrite existing tag"
    exit 1
  fi
fi

# Check if there are uncommitted changes
if ! git diff-index --quiet HEAD --; then
  echo -e "${YELLOW}Warning: You have uncommitted changes${NC}"
  read -p "Continue anyway? (y/n) " -n 1 -r
  echo
  if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${RED}Release cancelled${NC}"
    exit 1
  fi
fi

# Create the tag
echo -e "${GREEN}Creating tag ${TAG}...${NC}"
if ! git tag -a "$TAG" -m "Release ${VERSION}"; then
  echo -e "${RED}Error: Failed to create tag${NC}"
  exit 1
fi

echo -e "${GREEN}Tag created successfully${NC}"

# Push the tag
echo -e "${GREEN}Pushing tag to remote...${NC}"
if [ "$FORCE" = true ]; then
  if ! git push origin "$TAG" --force; then
    echo -e "${RED}Error: Failed to push tag to remote${NC}"
    echo -e "${YELLOW}Rolling back local tag...${NC}"
    git tag -d "$TAG"
    exit 1
  fi
else
  if ! git push origin "$TAG"; then
    echo -e "${RED}Error: Failed to push tag to remote${NC}"
    echo -e "Tag may already exist on remote. Use ${YELLOW}--force${NC} to overwrite"
    echo -e "${YELLOW}Rolling back local tag...${NC}"
    git tag -d "$TAG"
    exit 1
  fi
fi

echo -e "${GREEN}✓ Tag pushed successfully!${NC}"
echo ""
echo -e "${GREEN}Release process initiated!${NC}"
echo -e "GitHub Actions will now build and create the release."
echo -e "Check the progress at: ${YELLOW}https://github.com/$(git remote get-url origin | sed 's/.*github.com[:/]\(.*\)\.git/\1/')/actions${NC}"
echo ""
