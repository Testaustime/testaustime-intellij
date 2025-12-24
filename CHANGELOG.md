# testaustime-intellij Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed
- The editor name is now reported without the version number,
  e.g. `IntelliJ IDEA` instead of `IntelliJ IDEA 2025.2`.

### Removed
- IDE version `2022.2` is no longer supported, at least `2022.3` is now required.

## [0.3.1]

### Changed
- Fixed the plugin not working with some IntelliJ versions.
- Sealed a small memory leak with settings validation.

## [0.3.0]

### Added
- Project name is now correctly displayed
  for projects in directories with different names.

### Changed
- Coding time no longer increases while inactive.
- Hostname now works regardless of DNS settings.\*  
  <sub>*Works on Windows, macOS, and any OS with `gethostname()`.</sub>

## [0.2.0] - 2022-08-04

### Added
- Input verification on the settings page.
- Project-level Testaustime notifications.
### Changed
- Improved some English messages.
- Fixed tracking when multiple projects are open.
- Split notifications into Testaustime Information and Testaustime Warnings.
