import NextAuth from 'next-auth'
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
      console.log(token)
      console.log(user)
      console.log(account)
      console.log(profile)
      console.log(isNewUser)
      if (account?.accessToken) {
        token.accessToken = account.accessToken
      }
      return token
    }
  }
})
