import NextAuth, { DefaultSession } from 'next-auth'
import Providers from 'next-auth/providers'

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
      }
      return token
    },
    async session (session, userOrToken) {
      // Adding accessToken from JWT into Session
      type SessionWithAccessToken = DefaultSession & {
        user?: {
          accessToken: string
        }
      };
      const sessionWithAccess: SessionWithAccessToken = session as unknown as SessionWithAccessToken
      if (sessionWithAccess.user && userOrToken.accessToken) {
        sessionWithAccess.user.accessToken = userOrToken.accessToken as string
      }

      return sessionWithAccess
    }
  }
})
