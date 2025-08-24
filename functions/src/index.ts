import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

admin.initializeApp();

export const sendParcelNotification = functions.https.onCall(async (data, context) => {
    if (!context.auth) {
        throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }

    const { userId, title, body, parcelId } = data;

    // Get user's FCM token
    const userDoc = await admin.firestore().collection('users').doc(userId).get();
    const fcmToken = userDoc.get('fcmToken');

    if (!fcmToken) {
        throw new functions.https.HttpsError('failed-precondition', 'User has no FCM token');
    }

    // Send notification
    const message = {
        token: fcmToken,
        data: {
            title,
            body,
            parcelId
        }
    };

    try {
        await admin.messaging().send(message);
        return { success: true };
    } catch (error) {
        throw new functions.https.HttpsError('internal', 'Failed to send notification');
    }
}); 