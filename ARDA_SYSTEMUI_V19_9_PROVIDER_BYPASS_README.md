# ARDA CarSystemUI V19.9

Root cause confirmed from the Ubuntu source/runtime audit:

`SystemBarConfigsImpl.readConfigs()` returned through
`SystemUiWindowProvider` before applying ARDA left/right rail settings.

V19.9 behavior:

- Normal mode: preserves provider-owned top/bottom bars.
- ARDA custom mode: bypasses provider-owned fixed windows.
- Creates the required top/bottom/left/right windows through the existing
  internal system-bar path.
- Publishes actual applied runtime state after window creation.
- Disables the display-compat toolbar side assignment only while ARDA's
  custom rail mode is active.

Runtime preferences remain controlled by ArdaPlatform:

```text
arda_bottom_hvac_visible
arda_left_rail_when_hvac_hidden
arda_right_status_rail_enabled
```
