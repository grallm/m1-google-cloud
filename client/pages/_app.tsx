import type { AppProps } from 'next/app'
import { Provider } from 'next-auth/client'
import Header from '../components/Header'

import 'bootstrap/dist/css/bootstrap.css'

// The following import prevents a Font Awesome icon server-side rendering bug,
// where the icons flash from a very large icon down to a properly sized one:
import '@fortawesome/fontawesome-svg-core/styles.css'
// Prevent fontawesome from adding its CSS since we did it manually above:
import { config } from '@fortawesome/fontawesome-svg-core'
config.autoAddCss = false

function MyApp ({ Component, pageProps }: AppProps) {
  return (
    <Provider session={pageProps.session}>
      <Header/>

      <div className='bg-light' style={{ minHeight: '100vh', paddingTop: '57px' }}>
        <main className='pt-3 mx-auto' style={{ maxWidth: '992px' }}>
          <Component {...pageProps} />
        </main>
      </div>
    </Provider>
  )
}

export default MyApp
