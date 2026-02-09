# Changelog

All notable changes to the CubisLang Java SDK will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.4-SNAPSHOT] - 2026-02-09

### Added
- **Staging and committing translation changes** - In-memory caching system for translation modifications
  - Stage translation updates in memory before committing to files
  - Commit staged changes to persist modifications to locale files
  - Rollback capability to discard uncommitted changes
  - Thread-safe operations with concurrent access support
  - Preview staged changes before committing
  - Batch commit operations for multiple translations
  - Integration with existing translation loading and caching mechanisms

## [1.3.0-SNAPSHOT] - 2026-01-06

### Added
- **Write missing keys to file** - Async batch writing of missing translation keys directly to locale files
  - Non-blocking background processing with configurable batch size and flush interval
  - Missing keys automatically added to locale files (e.g., `en.json`, `km.json`) with empty values
  - Perfect for development workflows - translators can fill in values in actual locale files
  - Thread-safe with deduplication to prevent duplicate keys
  - Graceful shutdown ensures all keys are written before application exits
  - Pretty-printed JSON output maintains readable format
  - Configuration options:
    - `setWriteMissingKeysToFile(boolean)` - Enable/disable the feature (default: false)
    - `setMissingKeysBatchSize(int)` - Batch size threshold (default: 100)
    - `setMissingKeysFlushIntervalSeconds(int)` - Periodic flush interval (default: 30s)
  - New public method: `flushMissingKeys()` for manual flush trigger

### Changed
- Enhanced translation methods to record missing keys when write-to-file is enabled
- Improved shutdown process to flush remaining keys before cleanup

### Technical Details
- Uses `ScheduledExecutorService` for async background processing
- `ConcurrentHashMap` with `Set` for thread-safe key collection per locale
- Dual flush triggers: batch size threshold OR time interval
- Preserves existing translations while adding missing keys
- Creates new locale files if they don't exist

## [1.0.0] - 2026-01-05

### Added
- Initial release of CubisLang Java SDK
- Multi-locale translation support
- Load translations from local JSON files
- Load translations from remote CDN/URLs
- Automatic fallback to default locale
- Smart caching system for remote translations
- Encryption/decryption support for secure translations
- Template-based formatting with positional parameters
- Pluralization support
- Context-aware translations
- Keyword-based formatting (Mustache-style)
- Event listeners for translation loaded, errors, and missing keys
- Debug mode with comprehensive logging
- Thread-safe translation lookups
- Support for Console, Swing, and JavaFX applications
- Comprehensive API with builder pattern
- Built-in error handling and graceful degradation
- HTTP client for remote translation fetching
- Configurable cache duration
- Version-based cache revalidation

### Dependencies
- Gson 2.10.1 for JSON parsing
- SLF4J 2.0.9 for logging
- Logback 1.4.14 for logging implementation
- Unirest 4.4.4 for HTTP requests

### Requirements
- Java 8 or higher
- Gradle 8.x or Maven 3.x for building

### Documentation
- Complete README with usage examples
- API reference documentation
- Best practices guide
- Example implementation
- JSON translation file format specification
