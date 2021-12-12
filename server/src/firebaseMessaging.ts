import { t } from "schema/typesFactory"
import * as admin from "firebase-admin"
import { serviceAccount } from "PRIVATEserviceAccount"
import { ServiceAccount } from "firebase-admin"
import { MessagingPayload } from "firebase-admin/lib/messaging/messaging-api"

const app = admin.initializeApp({
  credential: admin.credential.cert(serviceAccount as ServiceAccount),
})

export const sendNotification = async (
  recipeientTokenOrTokens: string | string[] | undefined,
  payload: MessagingPayload,
) => {
  if (recipeientTokenOrTokens === undefined) return
  try {
    const response = await app
      .messaging()
      .sendToDevice(recipeientTokenOrTokens, payload)
    console.log("Successfully sent message:", response)
  } catch (e) {
    console.log("Error sending message:", e)
  }
}
