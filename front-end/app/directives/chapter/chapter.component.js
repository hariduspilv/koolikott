'use strict'

/**
 *  @todo
 -  tabIndexes
 -  Editor toolbar conf.
 -  WYSIWYG theme
 -  Material embeds
 -  Intermediary solution: embed materials BETWEEN blocks the old way
 -  position: sticky (for browsers that support it)
 */

{
/*rangy.init()
const ParagraphButton = MediumEditor.Extension.extend({
    name: 'p',
    tagNames: ['p'],
    init() {
        this.classApplier = rangy.createClassApplier('p', {
            elementTagName: 'p',
            normalize: true
        })
        this.button = this.document.createElement('button')
        this.button.classList.add('medium-editor-action')
        this.button.classList.add('medium-editor-action-paragraph')
        this.button.innerHTML = '<b>p</b>'
    },
    getButton() {
        return this.button
    },
    handleClick(evt) {
        evt.preventDefault()
        evt.stopPropagation()

        this.classApplier.toggleSelection()
        this.base.checkContentChanged()
    },
    /*isAlreadyApplied(node) {
        return node.nodeName.toLowerCase() === 'mark'
    },
    isActive() {
        return this.button.classList.contains('medium-editor-button-active')
    },
    setInactive() {
        this.button.classList.remove('medium-editor-button-active')
    },
    setActive() {
        this.button.classList.add('medium-editor-button-active')
    }*
})
const UnorderedListButton = MediumEditor.Extension.extend({
    name: 'ul'
})*/

const ALLOWED_TAGS = ['H3', 'P', 'BR', 'B', 'I', 'UL', 'LI', 'BLOCKQUOTE', 'DIV']
class ParagraphButton {
    constructor() {
        return new MediumButton({
            label: '<b>p</b>',
            action: (html, marked, parent) => {
                console.log({ html, marked, parent })

                /*if (parent.classList.contains('medium-editor-element'))

                else */if (parent.nodeType === Node.ELEMENT_NODE && parent.tagName !== 'P')
                    switch (parent.tagName) {
                        case 'UL': return this.transformUL(parent)
                        case 'LI': return this.transformLI(parent)
                    }

                return html
            }
        })
    }
    transformBlockLevelElement(parent) {

    }
    transformUL(parent) {
        this.saveSelection()

        const p = document.createElement('p')

        for (let li of parent.children)
            li.outerHTML = li.innerHTML

        parent.parentElement.insertBefore(p, parent)
        p.insertBefore(parent, null)
        parent.outerHTML = parent.innerHTML

        this.restoreSelection()

        return ''
    }
    transformLI(parent) {
        this.saveSelection()

        const p = document.createElement('p')
        const ul = this.getClosest('ul', parent)
        const idx = [].slice.call(parent.parentElement.children, 0).indexOf(parent)
        const extract = (after) => {
            p.insertBefore(parent, null)
            parent.outerHTML = parent.innerHTML
            ul.parentElement.insertBefore(p, after ? ul.nextSibling : ul)
        }
        const splitLists = (atIdx) => {
            const secondUL = document.createElement('ul')

            while (atIdx < ul.children.length) {
                secondUL.insertBefore(ul.children[atIdx], null)
                atIdx++
            }
            ul.parentElement.insertBefore(secondUL, ul.nextSibling)
        }

        ul.children.length === 1
            ? extract() || ul.parentElement.removeChild(ul)
            : idx === 0
                ? extract()
                : idx === ul.children.length - 1
                    ? extract(1)
                    : splitLists() || extract(1)

        this.restoreSelection()

        return ''
    }
    getClosest(tagName, el) {
        tagName = tagName.toUpperCase()
        
        let closest = el
        
        while (closest !== null) {
            if (closest.tagName === tagName)
                return closest

            closest = el.parentElement
        }
        
        return null
    }
    saveSelection() {
        const selection = window.getSelection()
        this.savedRange = selection.rangeCount && selection.getRangeAt(0)
    }
    restoreSelection() {
        if (this.savedRange) {
            const selection = window.getSelection()
            selection.removeAllRanges()
            selection.addRange(this.savedRange)
        }
    }
}
class controller extends Controller {
    $onChanges({ chapter }) {
        if (chapter && chapter.currentValue !== chapter.previousValue) {
            this.$scope.chapter = chapter.currentValue

            if (!this.isEditMode)
                this.$scope.chapter.title
                    ? this.$scope.chapterTitle = this.$scope.chapter.title
                    : this.$translate('PORTFOLIO_CHAPTER_TITLE_MISSING').then(missingTitle =>
                        this.$scope.chapterTitle = missingTitle
                    )

            // make sure there's always at least one empty block
            if (!Array.isArray(this.$scope.chapter.blocks))
                this.$scope.chapter.blocks = []

            if (!this.$scope.chapter.blocks.length)
                this.$scope.chapter.blocks.push({
                    narrow: false,
                    htmlContent: ''
                })
            
            // yes, we want it to run in the cycle after next
            if (this.isEditMode)
                this.$timeout(() =>
                    this.$timeout(() =>
                        this.updateEditors(/*true*/)
                    )
                )
        }
    }
    $onInit() {
        this.$scope.$watch('chapter.title', (title) =>
            this.$scope.slug = this.getSlug(title, `chapter-${this.index}`)
        )

        if (this.isEditMode) {
            this.$scope.isFocused = false
            this.$scope.isTitleFocused = false
            this.$scope.focusedBlockIdx = null
            this.$scope.stickyClassNames = {
                sticky: true,
                'is-sticky': false,
                'is-at-bottom': false
            }
            this.$scope.stickyStyle = {
                left: 0,
                top: 80 // site header height
            }

            this.$scope.$watch('chapter.blocks', this.onBlockChanges.bind(this), true)

            this.onClickOutside = this.onClickOutside.bind(this)
            document.addEventListener('click', this.onClickOutside)

            this.onResize = () => requestAnimationFrame(this.calcSizes.bind(this))
            window.addEventListener('resize', this.onResize)
            window.addEventListener('load', this.onResize)
            setTimeout(this.onResize)

            this.onScroll = () => this.$scope.isFocused && requestAnimationFrame(this.setStickyClassNames.bind(this))
            window.addEventListener('scroll', this.onScroll)
        }
    }
    $onDestroy() {
        if (this.isEditMode) {
            document.removeEventListener('click', this.onClickOutside)
            window.removeEventListener('resize', this.onResize)
            window.removeEventListener('scroll', this.onScroll)

            for (let el of this.getEditorElements()) {
                let editor = MediumEditor.getEditorFromElement(el)
                editor && editor.destroy()
            }
        }
    }
    onClickOutside(evt) {
        if (this.$scope.isFocused) {
            let el = evt.target

            while (el !== null) {
                if (el === this.$element[0])
                    return
                el = el.parentElement
            }

            this.$scope.isFocused = false
            this.$scope.isTitleFocused = false
            this.$scope.focusedBlockIdx = null
        }
    }
    onClickEmptyToolbarArea(evt) {
        let el = evt.target

        while (el !== null) {
            if (el.classList.contains('chapter-btn') ||
                el.classList.contains('chapter-btn-large'))
                return
            el = el.parentElement
        }

        this.$scope.isTitleFocused
            ? this.focusTitle(true)
            : this.focusBlock(this.$scope.focusedBlockIdx)
    }
    onClickToggleChapterFocus() {
        if (!this.$scope.isFocused)
            return this.focusTitle(true)

        this.$scope.isFocused = false
        this.$scope.isTitleFocused = false
        this.$scope.focusedBlockIdx = null
    }
    onClickBlock(idx, evt) {
        /**
         * For some reason ng-click fires also when space bar is pressed.
         */
        if (evt.type === 'click')
            this.focusBlock(idx)
    }
    onBlockChanges(blocks, previousBlocks) {
        this.$timeout(() => {
            for (let [idx, el] of this.getEditorElements().entries())
                this.createEditor(blocks, idx, el)
        })
    }
    createEditor(blocks, idx, el) {
        if (!MediumEditor.getEditorFromElement(el)) {
            const editor = new MediumEditor(el, {
                placeholder: idx === 0 && this.$scope.chapter.blocks.length === 1,
                toolbar: {
                    buttons: ['h3', 'p', 'anchor', 'bold', 'italic', 'quote', 'ul']
                },
                extensions: {
                    p: new ParagraphButton(),
                    ul: new MediumButton({
                        label: '<i class="fa fa-list-ul"></i>',
                        action(html, marked, parent) {
                            console.log({ html, marked, parent })
                            return html
                        }
                    })
                }
            })

            const saveSelection = editor.saveSelection.bind(editor)
            editor.subscribe('editableClick', saveSelection)
            editor.subscribe('editableKeyup', saveSelection)
            
            this.optimizePlaceholder(el, editor)
            this.disallowFormatting(el)
        }
    }
    updateEditors(safeMode = false) {
        /**
         * Update editor contents downstream:
         * $scope.chapter.blocks[idx].htmlContent -> editorElement.innerHTML
         */
        if (this.isEditMode)
            for (let [idx, el] of this.getEditorElements().entries())
                if (el && this.$scope.chapter.blocks[idx]) {
                    const editor = MediumEditor.getEditorFromElement(el)
                    if (editor) {
                        const selection = editor.exportSelection()

                        if (safeMode) {
                            editor.importSelection({
                                start: 0,
                                end: el.textContent.length
                            })
                            editor.pasteHTML(this.$scope.chapter.blocks[idx].htmlContent, {
                                cleanAttrs: ['style', 'dir']
                            })
                        } else
                            el.innerHTML = this.$scope.chapter.blocks[idx].htmlContent

                        editor.importSelection(selection)

                        if (el.innerHTML && el.innerHTML !== '<p><br></p>')
                            el.classList.remove('medium-editor-placeholder')
                    }
                }
    }
    updateState() {
        /**
         * Update input contents upstream:
         * editorElement.innerHTML -> $scope.chapter.blocks[idx].htmlContent
         */
        if (this.isEditMode)
            for (let [idx, el] of this.getEditorElements().entries())
                if (el && this.$scope.chapter.blocks[idx].htmlContent !== el.innerHTML) {
                    this.$scope.chapter.blocks[idx].htmlContent = el.innerHTML
                    MediumEditor.getEditorFromElement(el).restoreSelection()
                }
    }
    optimizePlaceholder(el, editor) {
        /**
         * Preventing placeholder flicker by delaying it's re-appearance
         */
        const removePlaceholder = (focused) => {
            this.$timeout.cancel(this.placeholderTimer)
            el.focused = focused
            el.classList.remove('medium-editor-placeholder')
        }
        editor.subscribe('focus', () => removePlaceholder(true))
        editor.subscribe('blur', () => {
            removePlaceholder(false)
            this.placeholderTimer = this.$timeout(() => {
                if (!el.focused && !el.innerHTML || el.innerHTML === '<p><br></p>')
                    el.classList.add('medium-editor-placeholder')
            }, 200)
        })
    }
    disallowFormatting(el) {
        /**
         * Unwrap html tags that are not allowed as soon as they are created.
         */
        const unwrapNodeIfNecessary = (node) => {
            if (!ALLOWED_TAGS.includes(node.tagName))
                node.outerHTML = node.innerHTML
        }
        if ('MutationObserver' in window) {
            el._mutationObserver = new MutationObserver(mutations =>
                mutations.forEach(({ addedNodes }) =>
                    addedNodes.forEach(unwrapNodeIfNecessary)
                )
            )
            el._mutationObserver.observe(el, {
                subtree: true,
                childList: true,
                characterData: true
            })
        } else
            el.addEventListener('DOMNodeInserted', (evt) =>
                unwrapNodeIfNecessary(evt.target)
            )
    }
    getEditorElements() {
        return this.$element[0].querySelectorAll('.chapter-block') || []
    }
    calcSizes() {
        if (this.isEditMode) {
            const sticky = this.$element[0].querySelector('.sticky')
            const { top, right, height } = this.$element[0].getBoundingClientRect()

            this.offsetTop = top + window.pageYOffset
            this.chapterHeight = this.$element[0].firstChild.offsetHeight
            this.toolbarHeight = sticky.offsetHeight
            this.$scope.stickyStyle.left = right
            this.$scope.stickyStyle.width = sticky.offsetWidth
        }
    }
    getChapterClassNames() {
        return !this.isEditMode ? {} : {
            'is-edit-mode': this.isEditMode,
            'is-focused': this.$scope.isFocused,
            'is-title-focused': this.$scope.isTitleFocused,
            'is-block-focused': this.$scope.focusedBlockIdx !== null
        }
    }
    getBlockClassNames(idx) {
        const { narrow } = this.$scope.chapter.blocks[idx]
        const classNames = {
            'is-narrow': narrow,
            'is-narrow-left': this.isNarrowLeft(idx),
            'is-focused': this.isEditMode && idx === this.$scope.focusedBlockIdx
        }
        return Object.assign(classNames, {
            'is-narrow-right': narrow && !classNames['is-narrow-left']
        })
    }
    getBlockPlaceholder(idx) {
        return idx === 0 && this.$scope.chapter.blocks.length === 1
            ? 'Alusta selle muutmisega (kliki siia) - lisa lõike, teksti, pilte, videosid, materjale e-koolikotist. Salvestamine toimub automaatselt. Jõudu tööle!'
            : ''
    }
    setStickyClassNames(evt) {
        const set = (isSticky, isAtBottom) => {
            if (this.$scope.stickyClassNames['is-sticky'] != isSticky)
                this.$scope.stickyClassNames['is-sticky'] = isSticky
            if (this.$scope.stickyClassNames['is-at-bottom'] != isAtBottom)
                this.$scope.stickyClassNames['is-at-bottom'] = isAtBottom

            // why doesn't it work without this!?
            if (evt)
                this.$scope.$apply()
        }

        if (this.chapterHeight - this.toolbarHeight < 100)
            return set(false, false)

        const toolbarTopMin = this.offsetTop - this.$scope.stickyStyle.top
        const toolbarTopMax = this.offsetTop + this.chapterHeight - this.toolbarHeight - this.$scope.stickyStyle.top
        const isAtBottom = window.pageYOffset >= toolbarTopMax
        const isSticky = !isAtBottom && window.pageYOffset >= toolbarTopMin

        set(isSticky, isAtBottom)
    }
    getToggleColumnWidthIcon() {
        const { narrow } = typeof this.$scope.focusedBlockIdx === 'number'
            ? this.$scope.chapter.blocks[this.$scope.focusedBlockIdx] || {}
            : {}

        return narrow
            ? '/images/chapter-toolbar-icon-column-wide.svg'
            : '/images/chapter-toolbar-icon-column-narrow.svg'
    }
    isNarrowLeft(idx) {
        let i = 0, colsFilled = 0
        
        for (let block of this.$scope.chapter.blocks) {
            if (this.$scope.chapter.blocks[idx].narrow &&
                i === idx &&
                !(colsFilled % 2)
            )
                return true

            colsFilled += block.narrow ? 1 : 2
            i ++
        }
    }
    focusChapter(evt) {
        // abort if a block or title was directly clicked
        let el = evt.target
        while (el !== null) {
            if (el.classList.contains('chapter-title') ||
                el.classList.contains('chapter-block'))
                return
            el = el.parentElement
        }
        /**
         * If title nor any of the blocks were directly clicked then focus
         * the last block (which is probably the only one and shorter than
         * the min-height of the whole chapter).
         */
        this.focusBlock()
    }
    focusTitle(clickOnContainer = false) {
        if (!this.$scope.isFocused) {
            this.calcSizes()
            this.setStickyClassNames()
        }
        this.$timeout(() => {
            this.$scope.isFocused = true
            this.$scope.isTitleFocused = true
            this.$scope.focusedBlockIdx = null

            if (clickOnContainer)
                this.$element[0].querySelector('input.md-headline').focus()
        })
    }
    focusBlock(idx, cb) {
        if (typeof idx !== 'number')
            idx = Math.max(0, this.$scope.chapter.blocks.length - 1)

        if (!this.$scope.isFocused) {
            this.calcSizes()
            this.setStickyClassNames()
        }

        this.$timeout(() => {
            this.$scope.isFocused = true
            this.$scope.isTitleFocused = false
            this.$scope.focusedBlockIdx = idx
            
            const el = this.getEditorElements()[idx]
            const editor = el && MediumEditor.getEditorFromElement(el)

            if (el)
                this.$timeout(() => {
                    el.focus()

                    if (editor) {
                        editor.trigger('focus')

                        // restore selection or put the caret to the end
                        if (editor.selectionState) {
                            editor.restoreSelection()
                            delete editor.selectionState
                        }
                        /**
                         * @todo Check if putting the caret to end works with embeds
                         */
                        else {
                            // get the lowest element that contains everything
                            let node = el
                            while (node.children.length === 1)
                                node = node.children[0]

                            const range = document.createRange()
                            range.selectNodeContents(node)
                            const { length } = range.toString()

                            editor.importSelection({ start: length, end: length })
                            editor.saveSelection()
                        }
                    }
                    if (typeof cb === 'function')
                        cb()
                })
        })
    }
    /**
     * Block actions
     */
    addBlock(htmlContent = '') {
        const { blocks } = this.$scope.chapter

        this.updateState()
        blocks.push({
            htmlContent,
            narrow: this.isNarrowLeft(blocks.length - 1)
        })
        this.focusBlock()
    }
    deleteBlock() {
        const { focusedBlockIdx, chapter: { blocks } } = this.$scope

        if (focusedBlockIdx !== null) {
            this.updateState()
            blocks.splice(focusedBlockIdx, 1)
            this.focusBlock(Math.min(focusedBlockIdx, blocks.length - 1))
            this.updateEditors()
        }
    }
    beforeToggleBlockWidth() {
        const { focusedBlockIdx } = this.$scope

        if (focusedBlockIdx !== null) {
            // save selection prior to mutation
            const editorElements = this.getEditorElements()
            const editor = MediumEditor.getEditorFromElement(editorElements[focusedBlockIdx])
            editor.saveSelection()
        }
    }
    toggleBlockWidth() {
        const { focusedBlockIdx, chapter: { blocks } } = this.$scope

        if (focusedBlockIdx !== null) {
            this.updateState()
            blocks[focusedBlockIdx].narrow = !blocks[focusedBlockIdx].narrow
            this.focusBlock(focusedBlockIdx)
        }
    }   
    beforeMoveBlock(up = false) {
        const { focusedBlockIdx } = this.$scope

        // swap and save selection states
        if (focusedBlockIdx !== null) {
            const newIdx = focusedBlockIdx + (up ? -1 : 1)
            const editorElements = this.getEditorElements()
            const fromEditor = MediumEditor.getEditorFromElement(editorElements[focusedBlockIdx])
            const toEditor = MediumEditor.getEditorFromElement(editorElements[newIdx])
            toEditor.importSelection(fromEditor.exportSelection())
            toEditor.saveSelection()
        }
    }
    moveBlock(up = false) {
        const { focusedBlockIdx, chapter: { blocks } } = this.$scope

        if (focusedBlockIdx !== null) {
            const newIdx = focusedBlockIdx + (up ? -1 : 1)

            this.updateState()
            blocks.splice(newIdx, 0, blocks.splice(focusedBlockIdx, 1)[0])
            this.focusBlock(newIdx)
            this.updateEditors()
        }
    }
    /**
     * @todo in MS 13: Embed actions
     */
    addMedia() {}
    addRecommendedMaterial() {}
    addExistingMaterial() {}
    addNewMaterial() {}
}
controller.$inject = [
    '$scope',
    '$rootScope',
    '$element',
    '$timeout',
    '$translate',
    'dialogService'
]
component('dopChapter', {
    bindings: {
        index: '<',
        numChapters: '<',
        chapter: '<',
        onMoveUp: '&',
        onMoveDown: '&',
        onDelete: '&',
        isEditMode: '<'
    },
    templateUrl: 'directives/chapter/chapter.html',
    controller
})
}
