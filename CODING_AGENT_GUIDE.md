# Coding Agent Quick Reference Guide

## Repository Structure

```
android-baby-sleep-training-lamp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/it/davidenastri/littlecloud/
│   │   │   │   ├── MainActivity.kt          # Main UI logic
│   │   │   │   └── QueryUtils.kt            # Particle API communication
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   └── activity_main.xml    # Main UI layout
│   │   │   │   ├── values/
│   │   │   │   │   ├── strings.xml          # String resources
│   │   │   │   │   ├── colors.xml           # Color definitions
│   │   │   │   │   └── styles.xml           # Theme styles
│   │   │   │   └── menu/
│   │   │   │       └── bottom_nav_menu.xml  # Navigation menu
│   │   │   └── AndroidManifest.xml          # App configuration
│   │   ├── test/                            # Unit tests
│   │   └── androidTest/                     # Instrumented tests
│   └── build.gradle                         # App dependencies
├── build.gradle                             # Project configuration
├── gradle.properties                        # Gradle settings
├── settings.gradle                          # Project modules
├── README.md                                # Project readme
├── APP_STATUS_AND_IMPROVEMENTS.md           # This status report
└── URGENT_IMPROVEMENTS.md                   # Priority fixes

```

## Build Commands

```bash
# Make gradlew executable (if needed)
chmod +x gradlew

# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run all checks
./gradlew check

# Run unit tests
./gradlew test

# Run lint
./gradlew lint

# Run instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest
```

## Key Configuration Files

### AndroidManifest.xml
Location: `app/src/main/AndroidManifest.xml`
- Declares app permissions
- Defines activities and components
- Sets app metadata

### build.gradle (app level)
Location: `app/build.gradle`
- App dependencies
- SDK versions
- Build configuration

### build.gradle (project level)
Location: `build.gradle`
- Gradle plugin version
- Repository definitions
- Kotlin version

## Testing Approach

### Without Hardware

#### ✅ Can Test:
1. **Code Compilation**
   ```bash
   ./gradlew assembleDebug
   ```

2. **Unit Tests**
   ```bash
   ./gradlew test
   ```

3. **Lint Checks**
   ```bash
   ./gradlew lint
   ```

4. **UI in Emulator**
   - Color picker interaction
   - Navigation between tabs
   - Settings form behavior
   - Button states

#### ⚠️ Need Mocks For:
1. **API Communication**
   - Use WireMock or similar
   - Mock Particle API responses
   - Test error scenarios

2. **SharedPreferences**
   - Use mock context
   - Test save/load operations

#### ❌ Cannot Test:
1. Actual lamp hardware responses
2. Real Particle Cloud API
3. DFPlayer audio playback

### Testing Strategy

```kotlin
// Example: Testing with mocks
@Test
fun testColorValidation() {
    val utils = QueryUtils
    // Test validation logic
    assertTrue(isValidRgbString("255,128,64,1000"))
    assertFalse(isValidRgbString("300,128,64,1000"))
}

// Example: Testing with Robolectric
@RunWith(RobolectricTestRunner::class)
class MainActivityTest {
    @Test
    fun testSettingsSave() {
        val activity = Robolectric.setupActivity(MainActivity::class.java)
        // Test settings persistence
    }
}
```

## Common Issues & Solutions

### Issue: Build fails with network error
**Cause:** Cannot access Google/Maven repositories
**Solution:** This is expected in sandboxed environments. Focus on code analysis and tests.

### Issue: Cannot run on device/emulator
**Cause:** No physical device or emulator available
**Solution:** Use Robolectric for local unit tests with Android framework.

### Issue: Missing INTERNET permission
**Cause:** Permission not declared in AndroidManifest
**Solution:** Add to AndroidManifest.xml (see URGENT_IMPROVEMENTS.md)

### Issue: App crashes on launch
**Possible Causes:**
1. Missing permissions
2. Unhandled exceptions in onCreate
3. View binding not initialized
4. Resource not found

**Debug Steps:**
1. Check logcat for stack trace
2. Verify AndroidManifest.xml is valid
3. Check resource IDs match layout
4. Verify ViewBinding is enabled

## Code Modification Guidelines

### Adding a Permission
```xml
<!-- AndroidManifest.xml -->
<manifest ...>
    <uses-permission android:name="android.permission.INTERNET" />
    <application ...>
        ...
    </application>
</manifest>
```

### Adding a Dependency
```gradle
// app/build.gradle
dependencies {
    implementation 'androidx.core:core-ktx:1.12.0'
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test:runner:1.5.2'
}
```

### Adding a String Resource
```xml
<!-- res/values/strings.xml -->
<resources>
    <string name="new_string">New Value</string>
</resources>
```

### Adding View Binding
```kotlin
// Already enabled in build.gradle
android {
    viewBinding {
        enabled = true
    }
}

// Usage in Activity
private lateinit var binding: ActivityMainBinding

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
}
```

## Validation Checklist

Before marking a task complete:

### Code Quality
- [ ] Code compiles without errors
- [ ] No new warnings introduced
- [ ] Code follows Kotlin conventions
- [ ] Proper error handling added
- [ ] Resources properly defined

### Testing
- [ ] Unit tests added (where applicable)
- [ ] Tests pass: `./gradlew test`
- [ ] Lint passes: `./gradlew lint`
- [ ] Manual testing in emulator (if UI changes)

### Documentation
- [ ] Code comments added for complex logic
- [ ] README updated if needed
- [ ] Improvement plan updated if needed

### Security
- [ ] No hardcoded secrets
- [ ] Proper permission declarations
- [ ] Input validation added
- [ ] Secure storage used for sensitive data

## Mock API Setup (Optional)

If you need to test API integration:

### Using WireMock

```kotlin
// Add test dependency
testImplementation 'com.github.tomakehurst:wiremock:2.27.2'

// Setup mock server
@Before
fun setup() {
    wireMockServer = WireMockServer(8080)
    wireMockServer.start()
    
    // Stub API response
    wireMockServer.stubFor(
        post(urlEqualTo("/device123/setColor"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("""{"return_value":1,"connected":true}""")
            )
    )
}

@After
fun tearDown() {
    wireMockServer.stop()
}
```

## Important Notes

### For Coding Agents

1. **Cannot build APK**: Due to network restrictions, full builds may fail. Focus on:
   - Code analysis
   - Unit tests (if they don't require network)
   - Static analysis
   - Code review

2. **Use offline mode**: If gradle sync fails
   ```bash
   ./gradlew --offline test
   ```

3. **Focus on**: 
   - Code correctness
   - Logic improvements
   - Test additions
   - Documentation

4. **Avoid**:
   - Trying to download dependencies
   - Running full builds
   - Accessing external APIs

### Architecture Overview

```
┌─────────────────┐
│  MainActivity   │  ← UI Layer (View Binding)
└────────┬────────┘
         │
         ├─→ Color Picker (Light Tab)
         ├─→ Music Player (Music Tab)
         └─→ Settings Form (Settings Tab)
         
         ↓
         
┌─────────────────┐
│   QueryUtils    │  ← Network Layer (AsyncHttpClient)
└────────┬────────┘
         │
         ↓
         
┌─────────────────┐
│  Particle API   │  ← External Service
│  (HTTPS REST)   │
└─────────────────┘
         │
         ↓
         
┌─────────────────┐
│  Physical Lamp  │  ← Hardware
│  (Photon/WiFi)  │
└─────────────────┘
```

### Data Flow

1. **Color Change**:
   - User picks color in ColorPickerView
   - MainActivity converts to RGB string
   - QueryUtils sends POST to Particle API
   - API forwards to lamp hardware
   - Lamp changes LED color

2. **Music Control**:
   - User presses play/pause/next/previous
   - MainActivity sends command to QueryUtils
   - QueryUtils posts to Particle dfMini endpoint
   - Lamp controls DFPlayer Mini module
   - Audio plays through speaker

3. **Settings**:
   - User enters API credentials
   - MainActivity validates input
   - Saves to SharedPreferences
   - QueryUtils reads on each API call

## Quick Reference: Key Classes

### MainActivity.kt
**Purpose**: Main activity handling UI and navigation
**Key Methods**:
- `setupNavigation()` - Configures bottom nav
- `handleLightNavigation()` - Shows light controls
- `handleMusicNavigation()` - Shows music controls
- `handleSettingsNavigation()` - Shows settings
- `handleFabButtonClick()` - Main action button
- `setupColorPicker()` - Configures color picker
- `saveSettings()` - Persists settings

### QueryUtils.kt
**Purpose**: Particle API communication
**Key Methods**:
- `changeColor(rgbString, colorSet, view)` - Send color to lamp
- `changeAudio(commandString, view)` - Send audio command
- `getParticleDetails(view)` - Load credentials
- `createRequestParams(tokenId, args)` - Build API params

## Resources

- **Particle API Docs**: https://docs.particle.io/reference/cloud-apis/api/
- **Android Developer Guide**: https://developer.android.com/guide
- **Kotlin Docs**: https://kotlinlang.org/docs/home.html
- **Material Design**: https://material.io/design

## Support

For issues or questions, refer to:
1. APP_STATUS_AND_IMPROVEMENTS.md - Comprehensive status
2. URGENT_IMPROVEMENTS.md - Priority fixes
3. README.md - Project overview
