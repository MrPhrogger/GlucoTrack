GLUCOTRACK - TECHNICAL PROJECT REPORT

1. OVERVIEW
GlucoTrack is a personalized health management application designed to help users track their blood glucose levels. The app focuses on user isolation, cloud-based data persistence, and context-aware health insights.

2. CORE FUNCTIONALITY
- User Authentication: Secure onboarding via email and password.
- Reading Management: Users can log glucose values (mg/dL), status (e.g., Fasting, After Meal), and personal notes.
- Dynamic Visuals: Readings are color-coded (Yellow for Low, Green for Normal, Red for High) for instant interpretation.
- Smart Insights: The app generates personalized "AI Tips" and activity suggestions based on the context of the reading.

3. DATA ARCHITECTURE
A. FIREBASE AUTHENTICATION
- Handles user sign-up and log-in.
- Provides a unique User ID (UID) that serves as the key for all data isolation.
- Supports persistent login states, allowing users to skip the login screen after their first session.

B. FIREBASE REALTIME DATABASE (CLOUD SYNC)
- Data is stored in a structured JSON tree: `readings/{userId}/{readingKey}`.
- This structure ensures that users only see their own readings.
- Enables cross-device synchronization; a user can log in on any device and see their full history.
- Real-time listeners automatically update the UI when data is added or deleted.

C. LOCAL DATA (ROOM)
- The app maintains a Room Database schema. While primary storage was moved to the cloud for multi-user support, the local architecture remains ready for offline-caching implementations.

4. SMART LOGIC (AI ENGINE)
Instead of a generic threshold, the app uses an internal "Context-Aware Engine" to interpret data:
- Contextual Analysis: It distinguishes between Fasting and Post-Prandial (after meal) ranges.
- Semantic Analysis: It scans user "Notes" for keywords like "exercise," "sick," or "stress" to adjust advice (e.g., suggesting a snack before exercise if sugar is borderline low).

5. TECHNICAL INTERWORKINGS
- Threading & Performance: All database and network operations are moved off the Main Thread using Java Threads and Firebase Listeners to ensure a smooth, 60fps UI.
- Edge-to-Edge Design: Uses Android WindowInsets to ensure the UI respects system bars (taskbar and navigation bar) on all modern devices.
- Material 3 Components: Uses Outlined TextInputLayouts and FloatingActionButtons for a modern, tactile feel.
- Fresh & Nature-Inspired Theme: A custom color palette (#5EBF95, #B3F9DB) creates a calm, health-focused environment.

6. CONCLUSION
GlucoTrack transitions from a simple local tracker to a robust, cloud-enabled health platform. It balances complex medical logic with a simple, high-performance user interface.
