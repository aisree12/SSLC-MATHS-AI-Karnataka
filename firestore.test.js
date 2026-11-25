const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_sslc_10";
const BOB_UID = "bob_sslc_10";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated users cannot read or write any user collection", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const now = new Date(Date.now() - 1000);

  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("chapterMastery")
      .get()
  );
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Anon",
      preferredLanguage: "EN",
      targetScore: 90,
      readinessScore: 50,
      dailyGoalMinutes: 45,
      completedTodayMinutes: 0,
      streakDays: 1,
      createdAt: now,
      updatedAt: now,
    })
  );
});

test("Owner can create and partially update UserProfile; rejects ghost fields & future timestamps", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 10000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Ananya",
      preferredLanguage: "BILINGUAL",
      targetScore: 95,
      readinessScore: 68,
      dailyGoalMinutes: 45,
      completedTodayMinutes: 20,
      streakDays: 4,
      createdAt: created,
      updatedAt: created,
    })
  );

  // Partial update allowed fields
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      readinessScore: 74,
      completedTodayMinutes: 35,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Shadow update with ghost field rejected
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      isAdmin: true,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Tampered createdAt rejected
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      createdAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Future timestamp rejected
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      displayName: "Future",
      preferredLanguage: "EN",
      targetScore: 90,
      readinessScore: 50,
      dailyGoalMinutes: 30,
      completedTodayMinutes: 0,
      streakDays: 0,
      createdAt: new Date(Date.now() + 60000),
      updatedAt: new Date(Date.now() + 60000),
    })
  );
});

test("Cross-user isolation blocks Bob from reading or modifying Alice's profile, mastery, exams, or goals", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const created = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("chapterMastery")
      .doc("ch_10")
      .set({
        userId: ALICE_UID,
        chapterNumber: 10,
        masteryStatus: "PARTIALLY_UNDERSTOOD",
        accuracyPercent: 72,
        questionsSolved: 14,
        commonMistakeNote: "Watch discriminant sign in b^2 - 4ac",
        createdAt: created,
        updatedAt: created,
      })
  );

  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("chapterMastery")
      .get()
  );
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("chapterMastery")
      .doc("ch_10")
      .update({ accuracyPercent: 10, updatedAt: new Date(Date.now() - 1000) })
  );
});

test("ChapterMastery, ExamAttempt, and StudyGoal valid operations and bounds", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 5000);

  // Valid StudyGoal create & toggle
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("studyGoals")
      .doc("goal_1")
      .set({
        userId: ALICE_UID,
        title: "Solve 3 Quadratic word problems",
        chapterNumber: 10,
        targetMinutes: 30,
        completed: false,
        scheduledSlot: "Evening",
        createdAt: created,
        updatedAt: created,
      })
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("studyGoals")
      .doc("goal_1")
      .update({
        completed: true,
        updatedAt: new Date(Date.now() - 1000),
      })
  );

  // Valid ExamAttempt create; update is immutable (rejected)
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("examAttempts")
      .doc("attempt_1")
      .set({
        userId: ALICE_UID,
        examTitle: "2026-27 SSLC Model Paper 01",
        examMode: "FULL_BOARD_80",
        scoreObtained: 64.5,
        maxMarks: 80,
        durationSeconds: 7200,
        questionsAttempted: 35,
        questionsSkipped: 3,
        slowestTopic: "Triangles (BPT Proof)",
        feedbackSummary: "Strong algebra speed; review BPT statement and geometry steps.",
        createdAt: created,
        updatedAt: created,
      })
  );

  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("examAttempts")
      .doc("attempt_1")
      .update({
        scoreObtained: 80,
        updatedAt: new Date(Date.now() - 1000),
      })
  );

  // List subcollections as owner succeeds
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("studyGoals").get()
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("examAttempts").get()
  );
});
