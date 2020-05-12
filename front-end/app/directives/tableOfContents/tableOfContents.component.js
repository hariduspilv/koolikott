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


        this.$scope.scrollHandler = (slug, title, evt) => {
            console.log(slug + title + evt)
        }

        this.$scope.gotoChapter = (slug, title, evt) => {
            evt.preventDefault()

            this.scrollToElement('#'+slug, 500, 80)

            if (window.innerWidth < BREAK_LG)
                this.$mdSidenav('left').close()


        }
       this.$scope.makeChapterUrl = (slug) => {
            return this.$location.absUrl().split('#')[0] + '-' + this.replaceSpacesAndCharacters(this.$scope.portfolio.title) + '#' + slug
        }

        this.$scope.$watch(() => document.getElementById(this.$location.hash()), (documentState) => {
            if (documentState !== null) {
                this.setScrollPositionBasedOnLocationHash()
            }
        })
    }

    setScrollPositionBasedOnLocationHash() {
        const setScroll = () => {
            const slug = this.$location.hash()
            const el = slug && document.getElementById(slug)

            if (el) {
                const { top } = el.getBoundingClientRect()
                window.scrollTo(0, top + window.pageYOffset - 80)
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
                        slug: this.getSlug(`peatukk-${idx + 1}`),
                        subChapters: blocks.reduce((subchapters, { htmlContent }) => {
                            const wrapper = document.createElement('div')
                            wrapper.innerHTML = htmlContent
                            return subchapters.concat(
                                [].slice.apply(wrapper.querySelectorAll('.subchapter')).map(el => {
                                    subIdx++
                                    return {
                                        title: el.textContent || this.$translate.instant('PORTFOLIO_ENTER_SUBCHAPTER_TITLE'),
                                        slug: this.getSlug(`alapeatukk-${idx + 1}-${subIdx}`)
                                    }
                                })
                            )
                        }, [])
                    }
                })
            )
        else
            this.$scope.chapters = null;
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
    templateUrl: '/directives/tableOfContents/tableOfContents.html',
    controller
})
}
