/** @type {import('next').NextConfig} */
module.exports = {
  reactStrictMode: true,
  images: {
    domains: ['img.20mn.fr', 'storage.cloud.google.com', 'storage.googleapis.com']
  },
  eslint: {
    dirs: ['pages', 'utils', 'components', 'entities']
  }
}
