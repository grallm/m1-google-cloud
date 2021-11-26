import NextAuth from "next-auth"
import Providers from "next-auth/providers"

export default NextAuth({
  providers: [
    Providers.Google({
      clientId: '616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com',
      clientSecret: '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'
    }),
  ]
})
