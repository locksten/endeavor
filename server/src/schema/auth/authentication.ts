import { ExpressContext } from "apollo-server-express/dist/ApolloServer"
import { verify } from "jsonwebtoken"

export type AuthTokenBody = { id: number }

export const accessTokenSecret = process.env.JWT_ACCESS_SECRET!

export const authentication = ({
  req: {
    headers: { authorization: authHeader },
  },
}: ExpressContext) => {
  if (!authHeader) return {}
  try {
    const authToken = authHeader.split(" ")[1]
    const { id } = verify(authToken, accessTokenSecret) as AuthTokenBody
    return { id }
  } catch (err /* as VerifyErrors */) {
    return {}
  }
}
