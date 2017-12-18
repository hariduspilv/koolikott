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
            // float left
            svgContent: '<path d="M27,25 L6,25 L6,23 L27,23 L27,25 Z M21,21 L6,21 L6,19 L21,19 L21,21 Z M27,17 L19,17 L19,15 L27,15 L27,17 Z M23,13 L19,13 L19,11 L23,11 L23,13 Z M27,7 L27,9 L19,9 L19,7 L27,7 Z M6,7 L17,7 L17,17 L6,17 L6,7 Z"></path>',
            action: () => {
                if (this.target) {
                    if (this.target.classList.contains('chapter-embed-card--float-right'))
                        this.target.classList.remove('chapter-embed-card--float-right')
                    this.target.classList.toggle('chapter-embed-card--float-left')
                    this.setState()
                }
            }
        }, {
            // set full width
            svgContent: '<path d="M27,25 L6,25 L6,23 L27,23 L27,25 Z M6,11 L27,11 L27,21 L6,21 L6,11 Z M27,7 L27,9 L6,9 L6,7 L27,7 Z"></path>',
            action: () => {
                this.target.classList.remove('chapter-embed-card--float-left')
                this.target.classList.remove('chapter-embed-card--float-right')
                this.setState()
            }
        }, {
            // float right
            svgContent: '<path d="M6,25 L6,23 L27,23 L27,25 L6,25 Z M6,21 L6,19 L21,19 L21,21 L6,21 Z M6,17 L6,15 L14,15 L14,17 L6,17 Z M6,13 L6,11 L10,11 L10,13 L6,13 Z M6,7 L14,7 L14,9 L6,9 L6,7 Z M27,7 L27,17 L16,17 L16,7 L27,7 Z"></path>',
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
            left: left + window.scrollX + (width / 2)  + 'px',
            top: top + window.scrollY + 'px',
        }

        this.$scope.buttons[0].active = this.target.classList.contains('chapter-embed-card--float-left')
        this.$scope.buttons[2].active = this.target.classList.contains('chapter-embed-card--float-right')
        this.$scope.buttons[1].active = !this.$scope.buttons[0].active && !this.$scope.buttons[2].active
    }
    show() {
        this.$timeout(() => this.setClassNames(true))
    }
    hide() {
        if (!this.mouseover)
            this.hideTimer = this.$timeout(
                this.setClassNames.bind(this, false),
                500
            )
    }
    setClassNames(isVisible) {
        this.$scope.classNames = {
            'medium-editor-toolbar': true,
            'medium-editor-stalker-toolbar': true,
            'medium-toolbar-arrow-under': true,
            'medium-editor-toolbar-active': isVisible,
        }
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
    templateUrl: 'directives/chapter/embedToolbar/embedToolbar.html',
    controller
})
}