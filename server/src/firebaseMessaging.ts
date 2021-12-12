import { t } from "schema/typesFactory"
import * as admin from "firebase-admin"
import { serviceAccount } from "PRIVATEserviceAccount"
import { ServiceAccount } from "firebase-admin"
import { MessagingPayload } from "firebase-admin/lib/messaging/messaging-api"

const app = admin.initializeApp({
  credential: admin.credential.cert(serviceAccount as ServiceAccount),
})

export const sendNotification = async (
  recipeientToken: string,
  payload: MessagingPayload,
) => {
  try {
    const response = await app
      .messaging()
      .sendToDevice(recipeientToken, payload)
    console.log("Successfully sent message:", response)
  } catch (e) {
    console.log("Error sending message:", e)
  }
}

export const mutationNotify = t.field({
  name: "notify",
  type: t.String,
  resolve: async (_, _args, { auth }) => {
    if (!auth.id) return "Unauthorized"

    // sendNotification()

    return "Sent?"
  },
})
