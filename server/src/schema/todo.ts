import { Todo as QTodo } from "zapatos/schema"

export { Todo as QTodo } from "zapatos/schema"
export type Todo = QTodo.JSONSelectable & { _type?: "Todo" }
