import { getPool } from "context"
import { db, dc } from "database"
import { sendNotification } from "firebaseMessaging"
import { TodoTask } from "schema/task"

export const sendReminderNotifications = async () => {
  const tasks = (await db
    .select("TodoTask", {
      reminderDate: dc.and(
        dc.isNotNull,
        dc.before(new Date(new Date().getTime() + 30 * 1000)),
      ),
    })
    .run(getPool())) as TodoTask[]
  tasks.forEach((task) => sendReminderNotification(task))
}

export const sendReminderNotification = async (task: TodoTask) => {
  const user = await db.selectOne("User", { id: task.userId }).run(getPool())
  if (!user?.firebaseToken) return
  if (!task.reminderDate) return

  await sendNotification(user.firebaseToken, {
    notification: {
      title: `${db.toDate(task.reminderDate).getHours()}:${db
        .toDate(task.reminderDate)
        .getMinutes()} ${task.title}`,
    },
  })

  await db
    .update("Task", { reminderDate: null }, { id: task.id })
    .run(getPool())
}
