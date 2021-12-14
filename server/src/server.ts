import {
  getIntrospectedSchema,
  minifyIntrospectionQuery,
} from "@urql/introspection"
import { ApolloServer } from "apollo-server-express/dist/ApolloServer"
import { getPool, newAppContext } from "context"
import express from "express"
import { Express } from "express-serve-static-core"
import * as fs from "fs"
import { getIntrospectionQuery } from "graphql"
import fetch from "node-fetch"
import { sendReminderNotifications } from "reminderNotifications"
import { initializeDbIfNotInitialized } from "schema/databaseInitialization"
import { schema } from "schema/schema"
import { setInterval } from "timers"

const server = new ApolloServer({
  schema,
  context: newAppContext,
})

server.start().then(() => {
  const app = express()
  server.applyMiddleware({ app })
  listen(app)
})

const listen = (app: Express) => {
  app.listen({ port: 4000 }, () => {
    fetch("http://localhost:4000/graphql", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        variables: {},
        query: getIntrospectionQuery({ descriptions: false }),
      }),
    })
      .then((result) => result.json())
      .then(({ data }) => {
        const minified = minifyIntrospectionQuery(getIntrospectedSchema(data))
        fs.writeFileSync(
          "./graphql-schema.generated.json",
          JSON.stringify(minified),
        )
      })

    console.log(`🚀 Server ready at http://localhost:4000${server.graphqlPath}`)

    initializeDbIfNotInitialized(getPool())

    setInterval(() => {
      sendReminderNotifications()
    }, 5 * 1000)
  })
}
