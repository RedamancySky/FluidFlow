# FluidFlow Gestures — Hệ Thống Điều Hướng Cử Chỉ Độc Lập Cho Android

Hệ thống cử chỉ điều hướng mượt mà độc lập giải quyết triệt để lỗi vô hiệu hóa cử chỉ gốc của Android khi dùng launcher bên thứ ba (Nova, Lawnchair, Niagara,...), đem lại trải nghiệm lướt mượt mà chuẩn hệ thống với hoạt ảnh bóng nước uốn lượn (Fluid Wave Animation) và phản hồi rung xúc giác (Haptic Feedback).

---

### User Review & Critical Decisions

> [!IMPORTANT]
> **Các quyết định đã xác nhận với người dùng:**
> - **Vùng kích hoạt cử chỉ**: Cả 3 cạnh (Cạnh trái, Cạnh phải và Cạnh đáy màn hình) nhằm bao quát toàn diện mọi thao tác điều hướng.
> - **Hiệu ứng thị giác**: Bóng nước động lượn sóng (Fluid wave teardrop physics) bám theo ngón tay kéo, kèm phản hồi rung xúc giác khi đạt ngưỡng kích hoạt.
> - **Hệ thống tác vụ mở rộng**: Không chỉ dừng lại ở Back, Home, Recents mà tích hợp sẵn Mở thông báo (Notifications), Đổi ứng dụng gần nhất (Quick Switch / Last App), Chụp màn hình (Screenshot), và Bảng cài đặt nhanh (Quick Settings).

---

### 1. Overview & Core Concept

- **Vấn đề cốt lõi**: Kể từ Android 10, thành phần điều hướng cử chỉ gốc (Quickstep) được gắn chặt với Launcher mặc định của máy. Khi người dùng chuyển sang launcher bên thứ 3, hệ thống thường ép quay về 3 phím điều hướng cổ điển hoặc gây giật lag, đứt gãy trải nghiệm vuốt.
- **Giải pháp**:
  - Xây dựng dịch vụ **`FluidGestureAccessibilityService`** sử dụng API `performGlobalAction()` của Android để thực thi trực tiếp các thao tác hệ thống chuẩn xác, không độ trễ.
  - Quản lý các lớp phủ viền cảm ứng mỏng (`OverlayManager`) nằm dọc cạnh trái, phải và đáy màn hình.
  - Vẽ động hiệu ứng giọt nước / đường cong Bezier biến dạng theo tọa độ kéo của ngón tay với biểu tượng biến đổi mượt mà (Morphing Icon) chỉ thị thao tác sắp diễn ra.
  - Cung cấp **Gesture Playground / Sandbox** ngay trong ứng dụng: Cho phép người dùng trực tiếp kéo thử cử chỉ, kiểm tra độ nhạy, điều chỉnh vùng chạm và độ dày viền theo thời gian thực mà không sợ kích hoạt nhầm thoát app khi đang cài đặt.

---

### 2. User Experience & Visual Design

#### Luồng trải nghiệm người dùng (User Flows)
1. **Màn hình chính (Dashboard & Trạng thái)**:
   - Thẻ hiển thị trạng thái Dịch vụ Cử chỉ (Bật/Tắt) với nút gạt lớn, hướng dẫn cấp quyền Accessibility & Vẽ đè lên ứng dụng khác (Draw over apps) trong 1 chạm.
   - Thước đo hiệu năng & chỉ số phản hồi (Latency, FPS của hoạt ảnh).
   - Truy cập nhanh vào 3 vùng cấu hình: **Viền Trái (Left Edge)**, **Viền Phải (Right Edge)**, **Viền Đáy (Bottom Bar)**.
2. **Khu vực Tinh chỉnh Cử chỉ (Gesture Customizer)**:
   - Cho phép gán từng hành vi:
     - Vuốt ngắn (Swipe)
     - Vuốt và giữ (Swipe & Hold)
     - Vuốt chéo lên / xuống (Diagonal Swipe)
   - Chọn hành động từ danh mục trực quan: *Back, Home, Recents, Previous App, Notification Shade, Quick Settings, Screenshot, Lock Screen*.
3. **Khu vực Căn chỉnh Vùng chạm (Edge Trigger Geometry)**:
   - Tùy biến vị trí (Vertical offset), chiều cao vùng nhận diện, độ dày viền (độ rộng touch target) và độ nhạy khoảng cách vuốt (Trigger Distance).
   - Tính năng tự động ẩn hoặc đẩy cao viền khi bàn phím mở (Keyboard offset).
4. **Interactive Sandbox / Studio Cử chỉ (Playground)**:
   - Khung mô phỏng toàn màn hình cho phép người dùng kéo thử viền trái, phải, đáy; nhìn ngắm hiệu ứng giọt nước biến dạng, cảm nhận nhịp rung xúc giác và xác nhận lệnh thao tác trước khi áp dụng.

#### Bản sắc Thiết kế & Giao diện (Visual Identity)
- **Phong cách**: Modern Cyber-Minimalism, Dark AMOLED posture kết hợp các mảng màu Neon Accent tạo cảm giác công nghệ cao, phản hồi nhanh và chính xác.
- **Bảng màu**:
  - Nền sâu: Deep Obsidian `#0B0F19`, Thẻ nổi `#151D2F` và `#1E293B`.
  - Màu nhấn chính (Primary Accent): Electric Cyan `#00E5FF` và Neon Violet `#8B5CF6`.
  - Chỉ báo hành động: Emerald Green `#10B981` (Thành công/Home), Amber `#F59E0B` (Quick switch), Rose `#F43F5E` (Back).
- **Hoạt ảnh & Phản hồi (Motion & Haptics)**:
  - Đường cong Bézier bậc 3 (Cubic Bezier) mô phỏng lực căng bề mặt của giọt nước: ngón tay kéo càng xa, giọt nước càng phồng và kéo căng theo.
  - Khi ngón tay vượt qua ngưỡng kích hoạt (Trigger Threshold), biểu tượng hành động bung mở với hiệu ứng lò xo (`spring()`) và máy rung nhẹ (Haptic Click).

---

### 3. Key Product Decisions & Trade-Offs

- **Quyết định 1: Dùng AccessibilityService + WindowManager Overlay thay vì chỉ nhận diện trong ứng dụng**
  - *Lý do*: Đây là cơ chế chuẩn trên Android để ứng dụng có quyền bắt cử chỉ toàn cục trên mọi launcher và ứng dụng khác, đồng thời gọi các hàm hệ thống (`GLOBAL_ACTION_BACK`, `GLOBAL_ACTION_HOME`, `GLOBAL_ACTION_RECENTS`) mà không cần can thiệp quyền Root máy.
- **Quyết định 2: Tích hợp Gesture Sandbox trực tiếp trong Compose**
  - *Lý do*: Giúp người dùng trải nghiệm tức thì hoạt ảnh sóng nước và thử nghiệm gán phím ngay trên màn hình cấu hình, không bị gián đoạn hay phải mở các app khác để kiểm tra.
- **Quyết định 3: Lưu trữ cấu hình cử chỉ bằng Preferences / DataStore gọn nhẹ**
  - *Lý do*: Việc đọc cấu hình vùng cảm ứng, độ nhạy và hành động gán cần tốc độ phản hồi cực nhanh (micro-seconds) khi ngón tay chạm vào màn hình.

---

### 4. Technical Architecture & Data Strategy *(Technical Reference)*

#### Sơ đồ Kiến trúc Hệ thống

```
┌──────────────────────────────────────────────────────────────────┐
│                      Android System UI                          │
└──────────────┬───────────────────────────────────┬───────────────┘
               │ (Touch Events on Edges)           │ (Global Actions)
┌──────────────▼───────────────────────────────────┴──────────────┐
│                  FluidGestureAccessibilityService                │
│  - performGlobalAction(BACK / HOME / RECENTS / NOTIF / ...)      │
│  - WindowManager: Left / Right / Bottom Touch Overlays           │
│  - HapticFeedbackManager (Vibrator API / VibrationEffect)       │
└──────────────────────────────┬───────────────────────────────────┘
                               │ Reads Config
┌──────────────────────────────▼───────────────────────────────────┐
│               GestureConfigRepository (State & Storage)          │
│  - Left Edge Config   (Swipe, Hold, Height, Width, Sensitivity)  │
│  - Right Edge Config  (Swipe, Hold, Height, Width, Sensitivity)  │
│  - Bottom Edge Config (Left/Center/Right Pill Actions)          │
│  - Visual & Haptic Settings (Color, Bubble Size, Vibration)      │
└──────────────────────────────▲───────────────────────────────────┘
                               │ StateFlow / Update
┌──────────────────────────────┴───────────────────────────────────┐
│                    FluidFlow UI (Jetpack Compose)                │
│ ┌─────────────────────────┐   ┌────────────────────────────────┐ │
│ │ DashboardScreen         │   │ GestureSettingsScreen          │ │
│ │ - Service Toggle & Perms│   │ - Map Actions to Gestures      │ │
│ │ - Active Zones Status   │   │ - Height / Sensitivity Sliders │ │
│ └─────────────────────────┘   └────────────────────────────────┘ │
│ ┌─────────────────────────┐   ┌────────────────────────────────┐ │
│ │ GesturePlaygroundScreen │   │ AppearanceSettingsScreen       │ │
│ │ - Real-time Fluid Wave  │   │ - Wave Colors & Bubble Themes  │ │
│ │ - Live Touch Calibrator │   │ - Vibration Intensity / Style  │ │
│ └─────────────────────────┘   └────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────┘
```

#### Mô hình Cử chỉ & Dữ liệu (Data Model)
- `GestureTriggerZone`: `LEFT_EDGE`, `RIGHT_EDGE`, `BOTTOM_EDGE_LEFT`, `BOTTOM_EDGE_CENTER`, `BOTTOM_EDGE_RIGHT`.
- `GestureAction`:
  - `BACK` (`GLOBAL_ACTION_BACK`)
  - `HOME` (`GLOBAL_ACTION_HOME`)
  - `RECENTS` (`GLOBAL_ACTION_RECENTS`)
  - `LAST_APP` (Chuyển nhanh ứng dụng vừa dùng)
  - `NOTIFICATIONS` (`GLOBAL_ACTION_NOTIFICATIONS`)
  - `QUICK_SETTINGS` (`GLOBAL_ACTION_QUICK_SETTINGS`)
  - `SCREENSHOT` (`GLOBAL_ACTION_TAKE_SCREENSHOT`)
  - `LOCK_SCREEN` (`GLOBAL_ACTION_LOCK_SCREEN`)
- `ZoneConfig`:
  - `swipeAction: GestureAction`
  - `swipeAndHoldAction: GestureAction`
  - `heightPercent: Float` (từ 20% đến 100% chiều cao màn hình)
  - `widthDp: Int` (độ rộng vùng nhạy cảm ứng 8dp - 32dp)
  - `verticalOffsetPercent: Float`
  - `sensitivityDistancePx: Float`
- `VisualHapticConfig`:
  - `waveColor: Long`
  - `accentColor: Long`
  - `hapticEnabled: Boolean`
  - `hapticStrength: Int` (Light, Medium, Strong)
  - `showIconPreview: Boolean`

#### Component tương tác & Bộ giả lập Fluid Canvas:
- Sử dụng Jetpack Compose `Canvas` + `pointerInput` với tính toán Bezier curve thời gian thực cho màn hình Playground và lớp phủ View overlay:
  - Khi touch down: Xác định gốc neo (anchor Y).
  - Khi drag: Tính toán đỉnh vòm giọt nước uốn cong mượt mà theo khoảng cách `deltaX` (với cạnh trái/phải) hoặc `deltaY` (với cạnh đáy).
  - Khi vượt ngưỡng: Đổi trạng thái `isTriggered = true`, đổi màu gradient và rung haptic.
  - Khi thả ngón tay (touch up): Thực thi hành động tương ứng hoặc co giọt nước về viền nếu chưa vượt ngưỡng.
