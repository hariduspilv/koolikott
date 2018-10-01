'use strict'

{
class controller extends Controller {
    $onInit() {

        this.$scope.location = (this.$location.absUrl().substring(0, this.$location.absUrl().indexOf('#'))) ? this.$location.absUrl().split('&chapterName')[0] : this.$location.absUrl()
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
        this.$scope.gotoChapter = (slug, title, evt) => {
            evt.preventDefault()

            const titleForUrl = this.replaceSpacesAndCharacters(title)

            history.pushState({}, '', this.$location.url().split('&chapterName')[0] + '&chapterName=' + titleForUrl + '#'+slug)
            this.scrollToElement('#'+slug, 500, 80)

            if (window.innerWidth < BREAK_LG)
                this.$mdSidenav('left').close()
        }
       this.$scope.makeChapterUrl = (slug, title) => {
            return this.$location.absUrl().split('&chapterName')[0] + '&chapterName=' + this.replaceSpacesAndCharacters(title) + '#' + slug
        }
    }

    replaceSpacesAndCharacters(title) {
        let titleForUrl = title.replace(/\s+/g, '_')
        titleForUrl = titleForUrl.normalize('NFD').replace(/[\u0300-\u036f]/g, "")
        titleForUrl = titleForUrl.substring(0, 20)
        return titleForUrl.replace(/[\W_]/g, "_")
    }


    setScrollPositionBasedOnLocationHash() {
        const setScroll = () => {
            const slug = this.$location.hash()
            const el = slug && document.getElementById(slug)

            if (el) {
                const { top } = el.getBoundingClientRect()
                window.scrollTo(0, top + window.scrollY - 80)
            }
        }
        document.readyState === 'complete'
            ? setScroll()
            : window.onload = setScroll
    }
    setChapters() {
        const { chapters } = this.$scope.portfolio || {}

        if (Array.isArray(chapters) && chapters.length && chapters[0].blocks)
            this.$translate.onReady().then(() =>
                this.$scope.chapters = chapters.map(({ title, blocks }, idx) => {
                    let subIdx = 0
                    return {
                        title: title || this.$translate.instant('PORTFOLIO_ENTER_CHAPTER_TITLE'),
                        slug: this.getSlug(`chapter-${idx + 1}`),
                        subChapters: blocks.reduce((subchapters, { htmlContent }) => {
                            const wrapper = document.createElement('div')
                            wrapper.innerHTML = htmlContent
                            return subchapters.concat(
                                [].slice.apply(wrapper.querySelectorAll('.subchapter')).map(el => {
                                    subIdx++
                                    return {
                                        title: el.textContent || this.$translate.instant('PORTFOLIO_ENTER_SUBCHAPTER_TITLE'),
                                        slug: this.getSlug(`subchapter-${idx + 1}-${subIdx}`)
                                    }
                                })
                            )
                        }, [])
                    }
                })
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
