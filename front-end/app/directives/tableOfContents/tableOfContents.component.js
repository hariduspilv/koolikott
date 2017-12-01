'use strict'

{
class controller extends Controller {
    $onInit() {
        this.$scope.authUser = this.authenticatedUserService.isAuthenticated()
        this.$scope.$watch(() => this.storageService.getPortfolio(), (portfolio) => {
            this.$scope.portfolio = portfolio
            this.setChapters()
        }, true)

        this.setScrollPositionBasedOnLocationHash()

        this.$scope.sortChapters = () => {
            this.updateChapterEditorsFromState.bind(this)
            this.setChapters()
        }
        this.$scope.gotoChapter = (slug, evt) => {
            evt.preventDefault()

            // setting hash directly causes page to jump instantly
            history.pushState({}, '', this.$location.url().split('#')[0]+'#'+slug)
            this.scrollToElement('#'+slug, 500, 80)

            if (window.innerWidth < BREAK_LG)
                this.$mdSidenav('left').close()
        }
    }
    setScrollPositionBasedOnLocationHash() {
        const setScroll = () => {
            const slug = this.$location.hash()
            const el = slug && document.getElementById(slug)

            if (el)
                window.scrollTo(0, el.offsetTop - 80)
        }
        document.readyState === 'complete'
            ? setScroll()
            : window.onload = setScroll
    }
    setChapters() {
        const { chapters } = this.$scope.portfolio || {}

        if (Array.isArray(chapters) && chapters.length && chapters[0].blocks)
            this.$translate.onReady().then(() =>
                this.$scope.chapters = chapters.map(({ title, blocks }, idx) => ({
                    title: title || this.$translate.instant('PORTFOLIO_ENTER_CHAPTER_TITLE'),
                    slug: this.getSlug(`chapter-${idx + 1}`),
                    subChapters: blocks.reduce((subchapters, { htmlContent }) => {
                        const wrapper = document.createElement('div')
                        wrapper.innerHTML = htmlContent
                        return subchapters.concat(
                            [].slice.apply(wrapper.querySelectorAll('.subchapter')).map((el, subIdx) => ({
                                title: el.textContent || this.$translate.instant('PORTFOLIO_ENTER_SUBCHAPTER_TITLE'),
                                slug: this.getSlug(`subchapter-${idx + 1}-${subIdx + 1}`)
                            }))
                        )
                    }, [])
                }))
            )
    }
}
controller.$inject = [
    '$scope',
    '$mdSidenav',
    '$document',
    '$location',
    '$timeout',
    '$translate',
    'storageService',
    'authenticatedUserService'
]
component('dopTableOfContents', {
    templateUrl: 'directives/tableOfContents/tableOfContents.html',
    controller
})
}
