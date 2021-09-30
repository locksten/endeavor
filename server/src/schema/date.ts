import { db } from "database"
import { t } from "schema/typesFactory"
import { TimestampTzString } from "zapatos/db"

export const DateType = t.scalarType<TimestampTzString>({
  name: "Date",

  parseValue: (value) =>
    db.toString(new Date(value as unknown as string), "timestamptz"),

  serialize: (value) => db.toDate(value),

  parseLiteral: (node) =>
    node.kind === "StringValue"
      ? db.toString(new Date(node.value), "timestamptz")
      : null,
})
