import NextAuth, { DefaultSession } from 'next-auth'
import Providers from 'next-auth/providers'
import { apiRoute } from '../../../utils/common.api'

export type SessionWithAccessToken = DefaultSession & {
  user?: {
    accessToken: string
  }
};

export default NextAuth({
  secret: process.env.JWT_SECRET,
  providers: [
    Providers.Google({
      clientId: '616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com',
      clientSecret: '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'
    })
  ],
  callbacks: {
    async jwt (token, user, account, profile, isNewUser) {
      // Adding Google Access Token in JWT
      // https://blog.srij.dev/nextauth-google-access-token
      if (account?.accessToken) {
        token.accessToken = account.accessToken

        // Register user on Java Server
        if (user) {
          fetch(`${apiRoute}/user?access_token=${account.accessToken}`, {
            method: 'POST',
            body: JSON.stringify({
              name: user.name || 'NAME',
              email: user.email || 'EMAIL'
            })
          })
        }
      }

      return token
    },
    async session (session, userOrToken) {
      // Adding accessToken from JWT into Session
      const sessionWithAccess: SessionWithAccessToken = session as unknown as SessionWithAccessToken
      if (sessionWithAccess.user && userOrToken.accessToken) {
        sessionWithAccess.user.accessToken = userOrToken.accessToken as string
      }

      return sessionWithAccess
    },
    async redirect (url, baseurl) {
      // Redirect to given URL
      return url || baseurl
    }
  }
})
