# Contributing to CubisLang

Thank you for your interest in contributing to CubisLang! This document provides guidelines for contributing to the project.

## Code of Conduct

By participating in this project, you agree to maintain a respectful and inclusive environment for all contributors.

## How to Contribute

### Reporting Bugs

If you find a bug, please create an issue on GitHub with:

-   Clear description of the problem
-   Steps to reproduce
-   Expected vs actual behavior
-   Java version and OS
-   Code sample if applicable

### Suggesting Features

Feature suggestions are welcome! Please create an issue with:

-   Clear description of the feature
-   Use case and benefits
-   Potential implementation approach (optional)

### Pull Requests

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass: `./gradlew test`
6. Commit with clear messages
7. Push to your fork
8. Create a Pull Request

## Development Setup

### Prerequisites

-   Java 8 or higher
-   Gradle 8.x
-   Git

### Clone and Build

```bash
git clone https://github.com/cubetiq/cubis-langx-java.git
cd cubis-langx-java
./gradlew build
```

### Run Tests

```bash
./gradlew test
```

### Run Example

```bash
./gradlew run
```

## Coding Standards

### Java Style

-   Follow Java naming conventions
-   Use meaningful variable and method names
-   Add JavaDoc comments for public APIs
-   Keep methods focused and concise

### Example

```java
/**
 * Retrieves a translated string for the given key.
 *
 * @param key The translation key
 * @return The translated string, or the key if not found
 */
public String get(String key) {
    // Implementation
}
```

### Code Organization

-   Keep classes focused on a single responsibility
-   Use appropriate access modifiers
-   Minimize dependencies
-   Follow existing patterns in the codebase

## Testing

-   Write unit tests for new features
-   Ensure tests are independent and repeatable
-   Use descriptive test names
-   Aim for high test coverage

### Test Example

```java
@Test
public void testGetTranslation() {
    CubisLang lang = new CubisLang(options);
    String result = lang.get("greeting");
    assertEquals("Hello!", result);
}
```

## Documentation

-   Update README.md for user-facing changes
-   Update USAGE_GUIDE.md for new features
-   Add JavaDoc comments for public APIs
-   Include code examples where appropriate

## Commit Messages

Use clear, descriptive commit messages:

```
Add support for nested translation keys

- Implement dot notation for nested keys
- Add tests for nested key resolution
- Update documentation with examples
```

Format:

-   First line: Brief summary (50 chars or less)
-   Blank line
-   Detailed description if needed
-   List specific changes with bullet points

## Release Process

1. Update version in build.gradle
2. Update CHANGELOG.md
3. Create release tag: `git tag -a v1.x.x -m "Release v1.x.x"`
4. Push tag: `git push origin v1.x.x`
5. Create GitHub release

## Areas for Contribution

### High Priority

-   Additional translation format support
-   Performance optimizations
-   More comprehensive tests
-   Better error handling

### Medium Priority

-   Additional examples (Spring Boot, Android)
-   IDE plugins (IntelliJ, Eclipse)
-   CLI tools for translation management
-   Translation validation tools

### Documentation

-   Tutorial videos
-   More use case examples
-   Translation to other languages
-   API reference improvements

## Questions?

-   Open an issue for questions
-   Check existing issues and discussions
-   Email: oss@cubetiqs.com

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

## Recognition

Contributors will be recognized in the project README and release notes.

Thank you for contributing to CubisLang!
