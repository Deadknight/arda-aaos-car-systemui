# ARDA CarSystemUI left/right rail final source

Runtime settings:

```text
arda_left_rail_when_hvac_hidden
arda_right_status_rail_enabled
arda_bottom_hvac_visible
```

Modes:

```text
left=0 right=0:
  normal top and bottom bars

left=1, HVAC visible:
  normal bottom bar; left mode waits

left=1, HVAC hidden:
  top status-only bar
  Back -> App Grid -> vertical dock -> Assistant -> Home on the left
  bottom bar removed

right=1:
  top bar removed
  Back remains in a narrow left rail
  right rail order:
    clock
    location/status
    notifications
    Bluetooth
    Wi-Fi/connectivity
    brightness
    volume
    driver avatar only

left=1 + HVAC hidden + right=1:
  full left navigation rail
  full right status rail
  no top or bottom bar
```

The existing Launcher `DockView.kt` must already contain vertical
`LinearLayoutManager` orientation support from V19.6 Stage 1.
The build script verifies that dependency and does not modify Launcher.
