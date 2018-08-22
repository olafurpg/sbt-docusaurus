// See https://docusaurus.io/docs/site-config.html for all the possible
// site configuration options.

const repoUrl = "https://github.com/olafurpg/sbt-docusaurus";
const gitterUrl = "https://gitter.im/olafurpg/sbt-docusaurus";

const siteConfig = {
  title: "sbt-docusaurus",
  tagline: "Easy to maintain open source documentation",
  url: "https://olafurpg.github.io/sbt-docusaurus",
  baseUrl: "/sbt-docusaurus/",

  // Used for publishing and more
  projectName: "sbt-docusaurus",
  organizationName: "olafurpg",

  // algolia: {
  //   apiKey: "???",
  //   indexName: "sbt-docusaurus"
  // },

  // For no header links in the top nav bar -> headerLinks: [],
  headerLinks: [
    { doc: "installation", label: "Docs" },
    { href: repoUrl, label: "GitHub", external: true }
  ],

  // If you have users set above, you add it here:
  // users,

  /* path to images for header/footer */
  headerIcon: "img/sbt-docusaurus.png",
  footerIcon: "img/sbt-docusaurus.png",
  favicon: "img/favicon.png",

  /* colors for website */
  colors: {
    primaryColor: "#267443",
    secondaryColor: "#181A1F"
  },

  customDocsPath: "out",

  // This copyright info is used in /core/Footer.js and blog rss/atom feeds.
  copyright: `Copyright © ${new Date().getFullYear()} Ólafur Páll Geirsson`,

  highlight: {
    // Highlight.js theme to use for syntax highlighting in code blocks
    theme: "github"
  },

  /* On page navigation for the current documentation page */
  onPageNav: "separate",

  /* Open Graph and Twitter card images */
  ogImage: "img/sbt-docusaurus.png",
  twitterImage: "img/sbt-docusaurus.png",

  editUrl: `${repoUrl}/edit/master/docs/`,

  repoUrl,
  gitterUrl
};

module.exports = siteConfig;
