import NextAuth from 'next-auth'
import Providers from 'next-auth/providers'
import { UserEntity } from '../../../entities/User.entity'
import { ApiEntity, apiRoute } from '../../../utils/common.api'

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
        token.userId = account.id

        // Register user on Java Server
        if (user) {
          await fetch(`${apiRoute}/user?access_token=${account.accessToken}`, {
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
      if (session.user && userOrToken.accessToken) {
        // Adding accessToken from JWT into Session
        session.user.accessToken = userOrToken.accessToken as string
        session.user.userId = userOrToken.userId as string

        // Add useful User data to session
        const res = await fetch(`${apiRoute}/user/fromToken?access_token=${userOrToken.accessToken}`)
        const userEntity = await res.json() as ApiEntity<UserEntity>

        session.user.listFollowing = userEntity?.properties.listFollowing || []
      }

      return session
    }
  }
})
