/** @type {import('next').NextConfig} */
module.exports = {
  reactStrictMode: true,
  images: {
    domains: ['img.20mn.fr']
  },
  eslint: {
    dirs: ['pages', 'utils', 'components', 'entities']
  }
}
