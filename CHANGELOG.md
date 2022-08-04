## Version 0.2.0

Version 0.2.0 comes with numerous small quality-of-life
improvements and fixes.
- Renamed some elements to align with the Testaustime naming style.
    - The settings page is now at <kbd>Settings</kbd> > <kbd>Tools</kbd> > <kbd>Testaustime</kbd>
    - All messages that previously used the incorrect capitalisation, TestausTime,
      have been updated to the correct capitalisation.
- Added input verification to the settings page.
    - The settings page will now notify the user of incorrect URLs or invalid tokens. It will
      not be possible to apply changes to settings unless both the API base URL and API token
      are valid. A blank token is allowed, however, to temporarily disable the API.
- Fixed multi-project tracking
    - Previously, if multiple projects were open, the plugin would
      only send heartbeats to the API for one project, and it would send
      disproportionally many of them. This is now fixed.
- Improved notifications
    - Notifications are now grouped into Testaustime Information and Testaustime Warnings.
      These may be adjusted in the IDE's notification settings.

> Warning  
> Changelog unavailable for versions below 0.2.0.