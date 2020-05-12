{
class controller extends Controller {
    $onChanges({ isVisible }) {
        if (isVisible && isVisible.currentValue !== isVisible.previousValue) {
            isVisible.currentValue && this.target instanceof HTMLElement
                ? this.setState() || this.show()
                : this.hide()
        }
    }
    $onInit() {
        this.$scope.buttons = [{
            name: 'float left',
            action: () => {
                if (this.target) {
                    if (this.target.classList.contains('chapter-embed-card--float-right'))
                        this.target.classList.remove('chapter-embed-card--float-right')
                    this.target.classList.toggle('chapter-embed-card--float-left')
                    this.setState()
                }
            }
        }, {
            name: 'full width',
            action: () => {
                this.target.classList.remove('chapter-embed-card--float-left')
                this.target.classList.remove('chapter-embed-card--float-right')
                this.setState()
            }
        }, {
            name: 'float right',
            action: () => {
                if (this.target) {
                    if (this.target.classList.contains('chapter-embed-card--float-left'))
                        this.target.classList.remove('chapter-embed-card--float-left')
                    this.target.classList.toggle('chapter-embed-card--float-right')
                    this.setState()
                }
            }
        }]
    }
    setState() {
        this.$timeout.cancel(this.hideTimer)

        const { left, top, width } = this.target.getBoundingClientRect()

        this.$scope.style = {
            left: left + window.pageXOffset + (width / 2)  + 'px',
            top: top + window.pageYOffset + 'px',
        }

        this.$scope.buttons[0].active = this.target.classList.contains('chapter-embed-card--float-left')
        this.$scope.buttons[2].active = this.target.classList.contains('chapter-embed-card--float-right')
        this.$scope.buttons[1].active = !this.$scope.buttons[0].active && !this.$scope.buttons[2].active
    }
    show() {
        this.$timeout(() => this.$scope.isVisible = true)
    }
    hide() {
        if (!this.mouseover)
            this.hideTimer = this.$timeout(
                () => this.$scope.isVisible = false,
                500
            )
    }
    onMouseEnter() {
        this.$timeout.cancel(this.hideTimer)
        this.mouseover = true
    }
    onMouseLeave() {
        this.$timeout.cancel(this.hideTimer)
        this.mouseover = false
        this.hide()
    }
}
controller.$inject = [
    '$scope',
    '$timeout',
]
component('dopEmbedToolbar', {
    bindings: {
        target: '<',
        isVisible: '<',
    },
    templateUrl: '/directives/embedToolbar/embedToolbar.html',
    controller
})
}
