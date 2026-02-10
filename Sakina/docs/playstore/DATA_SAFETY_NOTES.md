# Play Console – Data Safety notes (for Sakina)

These notes are meant to help you fill the Play Console **Data safety** form accurately based on the current codebase.

Last reviewed: 2026-02-09

## Data collected

### Location (Approximate)
- **Collected:** Yes (only if user grants location permission and triggers location fetch).
- **Purpose:** App functionality (calculate prayer times, show upcoming prayer).
- **Processed:** On-device.
- **Shared:** No.
- **Optional:** Yes (app can run without it, but prayer times require it).
- **Stored:** Yes (saved locally on device).

### Personal info (Name)
- **Collected:** Yes (user input).
- **Purpose:** App functionality (personalized greeting).
- **Processed:** On-device.
- **Shared:** No.
- **Optional:** Yes (can be left blank if you allow that in UX).
- **Stored:** Yes (saved locally on device).

### App activity (Prayer completion / checkmarks)
- **Collected:** Yes.
- **Purpose:** App functionality (progress tracking and celebration).
- **Processed:** On-device.
- **Shared:** No.
- **Optional:** N/A (core feature when using prayer tracking).
- **Stored:** Yes (saved locally on device).

### App settings (calculation method, madhab)
- **Collected:** Yes.
- **Purpose:** App functionality.
- **Processed:** On-device.
- **Shared:** No.
- **Stored:** Yes (DataStore).

## Data shared
- **No data is shared with third parties** by the app itself.
- The **Share** action uses Android’s share sheet; the user chooses another app to share text.

## Security practices
- Data is stored locally. No server-side storage is used by the app in the current implementation.

