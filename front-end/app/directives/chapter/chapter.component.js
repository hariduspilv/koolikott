'use strict'

/**
 @todo
 + embedas
    - bug: if material is last element, it can't be deleted
    - bug: if two materials are adjecent then they can only be deleted together
    - bug: if there's an empty paragraph between two embeds then it can't be deleted without deleting one of the embdeds ("backspace" deletes preceding embed and "delete" deletes the following)
    - bug: caret jumps to the end of block upon adding materials
 -  translations
*/

{
const ALLOWED_BLOCK_LEVEL_TAGS = ['H3', 'P', 'UL', 'LI', 'BLOCKQUOTE', 'DIV']
const ALLOWED_INLINE_TAGS = ['BR', 'B', 'I', 'A']
const ALLOWED_TAGS = ALLOWED_BLOCK_LEVEL_TAGS.concat(ALLOWED_INLINE_TAGS)
const ICON_SVG_CONTENTS = {
    h3: '<path d="M20,14.3400002 L21.4736328,14.34375 C22.5810602,14.34375 23.1347656,13.8017632 23.1347656,12.7177734 C23.1347656,12.2958963 23.002931,11.9516615 22.7392578,11.6850586 C22.4755846,11.4184557 22.103518,11.2851562 21.6230469,11.2851562 C21.2304668,11.2851562 20.8891616,11.3994129 20.5991211,11.6279297 C20.3090806,11.8564465 20.1640625,12.1406233 20.1640625,12.4804688 L17.2021484,12.4804688 C17.2021484,11.8066373 17.3896466,11.2060573 17.7646484,10.6787109 C18.1396503,10.1513646 18.6596646,9.73974757 19.324707,9.44384766 C19.9897494,9.14794774 20.7206991,9 21.5175781,9 C22.9414134,9 24.0605428,9.32519206 24.875,9.97558594 C25.6894572,10.6259798 26.0966797,11.5195256 26.0966797,12.65625 C26.0966797,13.207034 25.9282243,13.7270483 25.5913086,14.2163086 C25.2543928,14.7055689 24.7636751,15.1025375 24.1191406,15.4072266 C24.7988315,15.6533215 25.3320293,16.026853 25.71875,16.527832 C26.1054707,17.0288111 26.2988281,17.6484338 26.2988281,18.3867188 C26.2988281,19.5293026 25.8593794,20.4433559 24.9804688,21.1289062 C24.1015581,21.8144566 22.9472728,22.1572266 21.5175781,22.1572266 C20.6796833,22.1572266 19.9018591,21.9975602 19.184082,21.6782227 C18.466305,21.3588851 17.9228534,20.9165067 17.5537109,20.3510742 C17.1845685,19.7856417 17,19.1425817 17,18.421875 L19.9794922,18.421875 C19.9794922,18.8144551 20.1376937,19.1542954 20.4541016,19.4414062 C20.7705094,19.7285171 21.1601539,19.8720703 21.6230469,19.8720703 C22.1445339,19.8720703 22.5605453,19.7270522 22.8710938,19.4370117 C23.1816422,19.1469712 23.3369141,18.7763694 23.3369141,18.3251953 C23.3369141,17.6806608 23.1757829,17.2236342 22.8535156,16.9541016 C22.5312484,16.684569 22.0859403,16.5498047 21.5175781,16.5498047 L20,16.5498047 L20,14.3400002 Z M16,22 L13,22 L13,17 L9,17 L9,22 L6,22 L6,9 L9,9 L9,14 L13,14 L13,9 L16,9 L16,22 Z"></path>',
    p: '<path d="M20.9816895,17.2968826 C20.9816895,18.7734525 20.6491732,19.9526399 19.9841309,20.8344803 C19.3190885,21.7163206 18.2460994,22.1715698 17.1152344,22.1715698 C16.2421831,22.1715698 15.5625028,21.8489164 15,21.2102413 L15,25 L12,25 L12,11.9957809 L14.7685547,11.9957809 L14.8599997,12.8699999 C15.4283619,12.1668714 16.1718704,11.8199997 17.0976562,11.8199997 C18.2695371,11.8199997 19.3557096,12.2377886 20.0061035,13.1020508 C20.6564974,13.9663129 20.9816895,15.1542893 20.9816895,16.6660156 L20.9816895,17.2968826 Z M18.0197754,16.6220703 C18.0197754,14.9345619 17.352544,14.1051559 16.3681641,14.1051559 C15.6650355,14.1051559 15.2343762,14.3571066 15,14.8610153 L15,19.0953979 C15.2578138,19.6227443 15.6943325,19.8864136 16.3857422,19.8864136 C17.3291063,19.8864136 17.9904784,19.057633 18.0197754,17.4287186 L18.0197754,16.6220703 Z"></path>',
    anchor: '<path d="M15,12 L13,12 C10.790861,12 9,13.790861 9,16 C9,18.209139 10.790861,20 13,20 L15,20 L15,22 L17,22 L17,20 L19,20 C21.209139,20 23,18.209139 23,16 C23,13.790861 21.209139,12 19,12 L17,12 L17,10 L15,10 L15,12 Z M13,10 L19,10 C22.3137085,10 25,12.6862915 25,16 C25,19.3137085 22.3137085,22 19,22 L13,22 C9.6862915,22 7,19.3137085 7,16 C7,12.6862915 9.6862915,10 13,10 Z M12,15 L20,15 L20,17 L12,17 L12,15 Z"></path>',
    bold: '<path d="M19.6,15.79 C20.57,15.12 21.25,14.02 21.25,13 C21.25,10.74 19.5,9 17.25,9 L11,9 L11,23 L18.04,23 C20.13,23 21.75,21.3 21.75,19.21 C21.75,17.69 20.89,16.39 19.6,15.79 L19.6,15.79 Z M14,11.5 L17,11.5 C17.83,11.5 18.5,12.17 18.5,13 C18.5,13.83 17.83,14.5 17,14.5 L14,14.5 L14,11.5 Z M17.5,20.5 L14,20.5 L14,17.5 L17.5,17.5 C18.33,17.5 19,18.17 19,19 C19,19.83 18.33,20.5 17.5,20.5 Z"></path>',
    italic: '<polygon points="14 9 14 12 16.21 12 12.79 20 10 20 10 23 18 23 18 20 15.79 20 19.21 12 22 12 22 9"></polygon>',
    quote: '<path d="M10,21 L13,21 L15,17 L15,11 L9,11 L9,17 L12,17 L10,21 Z M18,21 L21,21 L23,17 L23,11 L17,11 L17,17 L20,17 L18,21 Z"></path>',
    unorderedlist: '<path d="M8,14.5 C7.17,14.5 6.5,15.17 6.5,16 C6.5,16.83 7.17,17.5 8,17.5 C8.83,17.5 9.5,16.83 9.5,16 C9.5,15.17 8.83,14.5 8,14.5 Z M8,8.5 C7.17,8.5 6.5,9.17 6.5,10 C6.5,10.83 7.17,11.5 8,11.5 C8.83,11.5 9.5,10.83 9.5,10 C9.5,9.17 8.83,8.5 8,8.5 Z M8,20.5 C7.17,20.5 6.5,21.18 6.5,22 C6.5,22.82 7.18,23.5 8,23.5 C8.82,23.5 9.5,22.82 9.5,22 C9.5,21.18 8.83,20.5 8,20.5 Z M11,23 L25,23 L25,21 L11,21 L11,23 Z M11,17 L25,17 L25,15 L11,15 L11,17 Z M11,9 L11,11 L25,11 L25,9 L11,9 Z"/>'
}
/*class CustomMediumEditorExtension {
    getClosestBlockLevelAncestor(el) {
        while(el !== null) {
            if (ALLOWED_BLOCK_LEVEL_TAGS.includes(el.tagName))
                return el
            el = el.parentElement
        }
    }
    getAncestorToMutate(el) {
        while(el !== null) {
            if (el.parentElement && (
                el.parentElement.tagName === 'BLOCKQUOTE' ||
                el.parentElement.classList.contains('medium-editor-element')))
                return el
            el = el.parentElement
        }
    }
    getEditorElement(el) {
        while(el !== null) {
            if (el.classList.contains('medium-editor-element'))
                return el
            el = el.parentElement
        }
    }
    getSelectionParentElement() {
        let parentEl = null, sel

        if (window.getSelection) {
            sel = window.getSelection()
            if (sel.rangeCount) {
                parentEl = sel.getRangeAt(0).commonAncestorContainer
                if (parentEl.nodeType != 1)
                    parentEl = parentEl.parentNode
            }
        } else if ((sel = document.selection) && sel.type != 'Control')
            parentEl = sel.createRange().parentElement()

        return parentEl
    }
}
class ParagraphButton extends CustomMediumEditorExtension {
    constructor(editorEl) {
        super()
        setTimeout(() =>
            this.monkeyPatchToolbar(editorEl)
        )
        this.mediumButton = new MediumButton({
            label: `<svg viewBox="0 0 32 32" preserveAspectRatio="xMidYMid meet">${ICON_SVG_CONTENTS.p}</svg>`,
            action: this.action.bind(this)
        })
        return this.mediumButton
    }
    action(html, marked, parent) {
        const selection = rangy.saveSelection()
        const editorEl = this.getEditorElement(parent)
        const editor = MediumEditor.getEditorFromElement(editorEl)
        const toolbar = editor.getExtensionByName('toolbar')
        const closestBlockLevelAncestor = this.getClosestBlockLevelAncestor(parent)
        
        if (closestBlockLevelAncestor.tagName === 'P') {
            rangy.restoreSelection(selection)
            return html
        }
        else if (closestBlockLevelAncestor.tagName === 'LI') {
            this.mutateListItem(closestBlockLevelAncestor)
        }
        else {
            const ancestorToMutate = this.getAncestorToMutate(parent)
            if (ancestorToMutate) {
                const p = document.createElement('p')

                editorEl.insertBefore(p, ancestorToMutate.nextSibling)
                p.insertBefore(ancestorToMutate, null)
                ancestorToMutate.outerHTML = ancestorToMutate.innerHTML

                // now see if there are other block level elements that shouldn't remain nested
                // in a paragraph (such as orphaned <li> elements).
                for (let el of p.querySelectorAll('li'))
                    el.outerHTML = el.innerHTML
            }
        }

        rangy.restoreSelection(selection)
        toolbar.setToolbarPosition()
        toolbar.checkState()
        return html
    }
    mutateListItem(li) {
        const p = document.createElement('p')
        const ul = li.parentElement
        const idx = [].slice.call(li.parentElement.children, 0).indexOf(li)
        const extract = (insertAfter) => {
            p.insertBefore(li, null)
            li.outerHTML = li.innerHTML
            ul.parentElement.insertBefore(p, insertAfter ? ul.nextSibling : ul)
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
    }
    monkeyPatchToolbar(editorEl) {
        /**
         * This way we can set active state to our custom button and
         * display the toolbar in the beginning of a new paragraph wihtout any selection.
         *
        const editor = MediumEditor.getEditorFromElement(editorEl)
        if (editor) {
            const toolbar = editor.getExtensionByName('toolbar')
            const origCheckState = toolbar.checkState.bind(toolbar)

            toolbar.checkState = () => {
                origCheckState()

                clearTimeout(this.checkTimer)
                this.checkTimer = setTimeout(() => {
                    const selectionParent = this.getSelectionParentElement()

                    if (selectionParent) {
                        const pButton = this.mediumButton.getButton()

                        // @todo Make this check of focused block more fail-safe
                        if (!selectionParent.textContent.trim() &&
                            toolbar.base.elements[0].classList.contains('is-focused')
                        ) {
                            toolbar .showToolbar()
                            toolbar.positionToolbar(window.getSelection())
                            toolbar.setToolbarButtonStates()
                        }

                        selectionParent && selectionParent.tagName === 'P'
                            ? pButton.classList.add('medium-editor-button-active')
                            : pButton.classList.remove('medium-editor-button-active')
                    }
                })
            }
        }
    }
}*/
/**
 * Display the toolbar in the beginning of a new paragraph wihtout any selection.
 */
class PreselectFormat {
    init() {
        setTimeout(() =>
            this.monkeyPatchToolbar()
        )
    }
    monkeyPatchToolbar() {
        const toolbar = this.base.getExtensionByName('toolbar')
        const origCheckState = toolbar.checkState.bind(toolbar)
        
        toolbar.checkState = () => {
            origCheckState()

            // clearTimeout(this.checkTimer)
            // this.checkTimer = setTimeout(() => {
                const selection = window.getSelection()

                if (selection.rangeCount) {
                    let selectionParent = selection.getRangeAt(0).commonAncestorContainer

                    if (selectionParent.nodeType != Node.ELEMENT_NODE)
                        selectionParent = selectionParent.parentNode

                    const selectionEditor = this.getSelectionEditor(selectionParent)

                    if (selectionParent &&
                        !selectionParent.textContent.trim() &&
                        this.base.elements[0] === selectionEditor
                    ) {
                        toolbar.showToolbar()
                        toolbar.setToolbarButtonStates()

                        /**
                         * this is necessary so that the toolbar would be positioned
                         * at the beginning of the empty row.
                         */
                        const range = document.createRange()
                        range.selectNodeContents(selectionParent)
                        selection.removeAllRanges()
                        selection.addRange(range)
                        
                        toolbar.positionToolbar(window.getSelection())
                    }
                }
            // }, 200)
        }
    }
    getSelectionEditor(el) {
        while (el !== null) {
            if (el.classList.contains('medium-editor-element'))
                return el
            el = el.parentElement
        }
    }
}
class controller extends Controller {
    $onChanges({ chapter }) {
        if (chapter && chapter.currentValue !== chapter.previousValue) {
            this.$scope.chapter = chapter.currentValue

            this.isEditMode
                ? !this.$scope.chapter.title && (this.$scope.chapter.title = '')
                : this.$scope.chapter.title
                    ? this.$scope.chapterTitle = this.$scope.chapter.title
                    : this.$translate('PORTFOLIO_ENTER_CHAPTER_TITLE').then(missingTitle =>
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
                        this.updateEditors()
                    )
                )
        }
    }
    $onInit() {
        this.$scope.$watch('chapter.title', (title) =>
            this.$scope.slug = this.getSlug(`chapter-${this.index + 1}`)
        )
        this.currentLanguage = this.translationService.getLanguage()

        if (this.isEditMode) {
            this.$scope.isFocused = false
            this.$scope.isTitleFocused = false
            this.$scope.focusedBlockIdx = null
            this.$scope.stickyClassNames = {
                sticky: true,
                'is-sticky': false,
                'is-at-bottom': false
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

            this.unsubscribeInsertMaterials = this.$rootScope.$on('chapter:insertMaterials', this.onInsertExistingMaterials.bind(this))
        } else
            this.$timeout(() => {
                for (let el of this.getEditorElements())
                    this.loadEmbeddedContents(el)
            })
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

            this.unsubscribeInsertMaterials()
        }
    }
    onClickOutside(evt) {
        if (this.$scope.isFocused) {
            let el = evt.target

            while (el !== null) {
                if (el === this.$element[0] || el.classList.contains('medium-editor-toolbar'))
                    return
                el = el.parentElement
            }

            this.$scope.isFocused = false
            this.$scope.isTitleFocused = false
            this.$scope.focusedBlockIdx = null
        }
    }
    onClickEmptyChapterArea(evt) {
        let el = evt.target

        while (el !== null) {
            if (el.classList.contains('chapter-block') ||
                el.classList.contains('chapter-title') ||
                el.classList.contains('chapter-btn') ||
                el.classList.contains('chapter-btn-large') ||
                el.classList.contains('chapter-pencil-button'))
                return
            el = el.parentElement
        }

        this.$scope.isTitleFocused
            ? this.focusTitle(true)
            : this.focusBlock(this.$scope.focusedBlockIdx, false, false, true)
    }
    onBlockChanges(blocks, previousBlocks) {
        // block elements are not in DOM before next loop
        this.$timeout(() => {
            for (let [idx, el] of this.getEditorElements().entries())
                this.createEditor(blocks, idx, el)
        })
    }
    createEditor(blocks, idx, el) {
        if (!MediumEditor.getEditorFromElement(el)) {
            const svg = { open: '<svg viewBox="0 0 32 32" preserveAspectRatio="xMidYMid meet">', close: '</svg>' }
            const editor = new MediumEditor(el, {
                placeholder: idx === 0 && this.$scope.chapter.blocks.length === 1,
                toolbar: {
                    buttons: ['h3', /*'p',*/ 'anchor', 'bold', 'italic', 'quote', 'unorderedlist'].map(name => ({
                        name,
                        contentDefault: `<svg viewBox="0 0 32 32" preserveAspectRatio="xMidYMid meet">${ICON_SVG_CONTENTS[name]}</svg>`
                    }))
                },
                extensions: {
                    // p: new ParagraphButton(el),
                    preselectFormat: new PreselectFormat()
                },
                updateOnEmptySelection: true
            })

            const saveSelection = editor.saveSelection.bind(editor)
            editor.subscribe('editableClick', saveSelection)
            editor.subscribe('editableKeyup', saveSelection)
            editor.subscribe('focus', this.onFocusBlock.bind(this, idx))
            editor.subscribe('blur', this.onBlurBlock.bind(this, idx))

            this.optimizePlaceholder(el, editor)
            this.watchEditableNodes(el)
        }
    }
    updateEditors() {
        /**
         * Update editor contents downstream:
         * $scope.chapter.blocks[idx].htmlContent -> editorElement.innerHTML
         */
        if (this.isEditMode)
            for (let [idx, el] of this.getEditorElements().entries())
                if (el && this.$scope.chapter.blocks[idx]) {
                    const editor = MediumEditor.getEditorFromElement(el)
                    if (editor) {
                        el.innerHTML = this.$scope.chapter.blocks[idx].htmlContent

                        if (el.innerHTML && el.innerHTML !== '<p><br></p>')
                            el.classList.remove('medium-editor-placeholder')

                        this.registerSubchapters(el)
                        this.loadEmbeddedContents(el)
                    }
                }
    }
    updateState() {
        /**
         * Update input contents upstream:
         * editorElement.innerHTML -> $scope.chapter.blocks[idx].htmlContent
         */
        const sanitizeEditedHtml = (html) => {
            const wrapper = document.createElement('div')
            wrapper.innerHTML = html
            
            // 1) remove id atts from subchapters
            for (let subchapter of wrapper.querySelectorAll('.subchapter'))
                subchapter.removeAttribute('id')

            // 2) reduce embedded materials to <div> without any content
            for (let embed of wrapper.querySelectorAll('.chapter-embed-card')) {
                embed.classList.remove('chapter-embed-card--loaded')
                embed.removeAttribute('contenteditable')
                while (embed.firstChild)
                    embed.removeChild(embed.firstChild)
            }

            return wrapper.innerHTML
        }
        if (this.isEditMode)
            for (let [idx, el] of this.getEditorElements().entries()) {
                const sanitizedHtml = sanitizeEditedHtml(el.innerHTML)

                if (this.$scope.chapter.blocks[idx] &&
                    this.$scope.chapter.blocks[idx].htmlContent !== sanitizedHtml
                )
                    this.$scope.chapter.blocks[idx].htmlContent = sanitizedHtml
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
    watchEditableNodes(el) {
        /**
         * 1) Unwrap html tags that are not allowed as soon as they are created.
         * 2) (Un)register subchapters upon creation/removal of <h3>
         */
        const handleAddedNode = (node) => {
            if (node.parentNode && node.nodeType === Node.ELEMENT_NODE) {
                if (!ALLOWED_TAGS.includes(node.tagName))
                    node.outerHTML = node.innerHTML
                else if (node.tagName === 'H3') {
                    node.classList.add('subchapter')
                    this.updateState()
                    this.registerSubchapters(this.getClosestEditor(node))
                }
            }
        }
        const handleRemovedNode = (node) => {
            if (node.nodeType === Node.ELEMENT_NODE && node.tagName === 'H3')
                this.updateState()
        }
        if ('MutationObserver' in window) {
            el._mutationObserver = new MutationObserver(mutations =>
                mutations.forEach(({ addedNodes, removedNodes }) => {
                    addedNodes.forEach(handleAddedNode)
                    removedNodes.forEach(handleRemovedNode)
                })
            )
            el._mutationObserver.observe(el, {
                subtree: true,
                childList: true,
                characterData: true
            })
        } else {
            el.addEventListener('DOMNodeInsertedIntoDocument', (evt) =>
                handleAddedNode(evt.target)
            )
            el.addEventListener('DOMNodeRemovedFromDocument', (evt) =>
                handleRemovedNode(evt.target)
            )
        }
    }
    registerSubchapters(el) {
        // add id attributes to all subchapters derived from subchapter titles
        if (el)
            for (let [subIdx, subEl] of el.querySelectorAll('.subchapter').entries())
                subEl.id = this.getSlug(`subchapter-${this.index + 1}-${subIdx + 1}`)
    }
    loadEmbeddedContents(el) {
        const encodeHtmlEntities = (str) => str.replace(/[\u00A0-\u9999<>\&]/gim, (i) =>
            '&#' + i.charCodeAt(0) + ';'
        )

        for (let embed of el.querySelectorAll('.chapter-embed-card')) {
            embed.setAttribute('contenteditable', 'false')

            const { id } = embed.dataset
            if (id)
                this.serverCallService
                    .makeGet('rest/material', { id })
                    .then(({ data }) => {
                        // @see https://coderwall.com/p/o9ws2g/why-you-should-always-append-dom-elements-using-documentfragments
                        const fragment = document.createDocumentFragment()
                        const { picture, publishers, authors, titles, source, uploadedFile, language, resourceTypes } = data

                        // insert thumbnail
                        if (picture && picture.name) {
                            const thumb = document.createElement('div')
                            thumb.classList.add('thumbnail')
                            thumb.style.backgroundImage = `url('/rest/picture/thumbnail/lg/${picture.name}')`
                            fragment.appendChild(thumb)
                        }

                        // caption with icon, title, publishers, authors & source link
                        const caption = document.createElement('div')
                        caption.classList.add('caption')
                        fragment.appendChild(caption)

                        // icon
                        // @todo derive specific material icon from resourceTypes
                        caption.innerHTML = `<md-icon class="material-icons">${this.iconService.getMaterialIcon(resourceTypes)}</md-icon>`

                        // title
                        const title = document.createElement('h5')
                        title.textContent = this.getUserDefinedLanguageString(titles, this.currentLanguage, language) || ''
                        caption.appendChild(title)

                        // publishers & authors
                        const publishersAndAuthors = document.createElement('p')
                        publishersAndAuthors.classList.add('chapter-embed-card__publishers-and-authors')
                        publishersAndAuthors.textContent = publishers.map(p => p.name).concat(authors.map(a => a.name+' '+a.surname)).join(', ')
                        caption.appendChild(publishersAndAuthors)

                        // source link
                        const sourceLink = document.createElement('p')
                        sourceLink.classList.add('chapter-embed-card__source')
                        sourceLink.innerHTML = `
                            <span>${this.$translate.instant('SOURCE_BIG')}</span>:
                            <a href="${source || uploadedFile.url || ''}" target="_blank">${source || uploadedFile.url || ''}</a>`
                        caption.appendChild(sourceLink)

                        embed.appendChild(fragment)
                        embed.classList.add('chapter-embed-card--loaded')
                    })
        }
    }
    getEditorElements() {
        return this.$element[0].querySelectorAll('.chapter-block') || []
    }
    getEditor(idx) {
        return MediumEditor.getEditorFromElement(this.getEditorElements()[idx])
    }
    getClosestEditor(child) {
        let el = child
        while(el !== null) {
            if (el.classList.contains('medium-editor-element'))
                return el
            el = el.parentElement
        }
    }
    calcSizes() {
        if (this.isEditMode) {
            this.sticky = this.sticky || this.$element[0].querySelector('.sticky')

            const { top } = this.sticky.getBoundingClientRect()

            this.offsetTop = top + window.pageYOffset
            this.chapterHeight = this.$element[0].firstChild.offsetHeight
            this.toolbarHeight = this.sticky.offsetHeight
            this.stickyTop = window.innerWidth < 960 ? 56 : 80
        }
    }
    getChapterClassNames() {
        return Object.assign({
            'is-edit-mode': this.isEditMode
        }, this.isEditMode ? {
            'is-focused': this.$scope.isFocused,
            'is-title-focused': this.$scope.isTitleFocused,
            'is-block-focused': this.$scope.focusedBlockIdx !== null
        } : {
            'is-content-empty':
                !Array.isArray(this.$scope.chapter.blocks) ||
                !this.$scope.chapter.blocks.length ||
                !this.$scope.chapter.blocks.find(b => b.htmlContent)
        })
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
            if (this.$scope.stickyClassNames['is-sticky'] != isSticky) {
                this.$scope.stickyClassNames['is-sticky'] = isSticky
                Stickyfill[isSticky ? 'addOne' : 'removeOne'](this.sticky)
            }
            if (this.$scope.stickyClassNames['is-at-bottom'] != isAtBottom)
                this.$scope.stickyClassNames['is-at-bottom'] = isAtBottom

            if (evt)
                this.$scope.$apply()
        }

        if (this.chapterHeight - this.toolbarHeight < 100)
            return set(false, false)

        const toolbarTopMin = this.offsetTop - this.stickyTop
        const toolbarTopMax = this.offsetTop + this.chapterHeight - this.toolbarHeight - this.stickyTop
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
    focusTitle(clickOnContainer = false) {
        this.$timeout.cancel(this.blurTimer)

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
    focusBlock(idx, scrollTo = false, restoreSelection = false, putCaretToEnd = false) {
        if (typeof idx !== 'number')
            idx = Math.max(0, this.$scope.chapter.blocks.length - 1)

        const editorEl = this.getEditorElements()[idx]
        const editor = editorEl && MediumEditor.getEditorFromElement(editorEl)

        if (editor) {
            editorEl.focus()

            if (scrollTo)
                this.scrollToElement()

            if (restoreSelection && editor.selectionState) {
                editor.restoreSelection()
                delete editor.selectionState
            }

            if (putCaretToEnd) {
                // get the lowest element that contains everything
                let node = editorEl
                while (node.children.length === 1)
                    node = node.children[0]

                const range = document.createRange()
                range.selectNodeContents(node)
                const { length } = range.toString()

                editor.importSelection({ start: length, end: length })
            }
        }
    }
    onFocusBlock(idx) {
        this.$timeout.cancel(this.blurTimer)

        if (!this.$scope.isFocused) {
            this.calcSizes()
            this.setStickyClassNames()
        }
        this.$timeout(() => {
            this.$scope.isFocused = true
            this.$scope.isTitleFocused = false
            this.$scope.focusedBlockIdx = idx
        })
    }
    blurTitle() {
        this.$timeout.cancel(this.blurTimer)

        this.blurTimer = this.$timeout(() => {
            if (this.$scope.focusedBlockIdx === null)
                this.$scope.isFocused = false
        }, 500)
    }
    onBlurBlock(idx) {
        this.$timeout.cancel(this.blurTimer)

        /**
         * If focusedBlockIdx is still the same in next event loop then focus has left the blocks
         * and it should be set to null.
         */
        this.blurTimer = this.$timeout(() => {
            if (this.$scope.focusedBlockIdx === idx) {
                this.$scope.focusedBlockIdx = null

                if (!this.$scope.isTitleFocused)
                    this.$scope.isFocused = false
            }
        }, 500)
    }
    detachEditorListener(editor, eventName, fnName) {
        for (let listener of editor.events.customEvents[eventName])
            if (listener.name.includes(fnName)) {
                editor.unsubscribe(eventName, listener)
                return listener
            }
    }
    /**
     * Block actions
     */
    addBlock(htmlContent = '') {
        this.$timeout.cancel(this.blurTimer)

        const { blocks } = this.$scope.chapter

        this.updateState()
        blocks.push({
            htmlContent,
            narrow: this.isNarrowLeft(blocks.length - 1)
        })
        /**
         * Have to wait 2 loops:
         * 1) block element to be injected to DOM
         * 2) Medium Editor to be initialized on it
         */
        this.$timeout(() =>
            this.$timeout(() =>
                this.focusBlock(blocks.length - 1, true)
            )
        )
    }
    deleteBlock() {
        this.$timeout.cancel(this.blurTimer)

        const { focusedBlockIdx, chapter: { blocks } } = this.$scope
        const deleteBlock = () => {
            this.updateState()
            blocks.splice(focusedBlockIdx, 1)
            this.updateEditors()
            this.focusBlock(Math.min(focusedBlockIdx, blocks.length - 1), false, false, true)
        }

        if (focusedBlockIdx !== null) {
            const { innerHTML } = this.getEditorElements()[focusedBlockIdx]

            /**
             * @todo Properly restore selection if delete confirmation is declined
             */
            innerHTML && innerHTML !== '<p><br></p>'
                ? this.dialogService.showDeleteConfirmationDialog('ARE_YOU_SURE_DELETE', '', deleteBlock)
                : deleteBlock()
        }
    }
    beforeToggleBlockWidth() {
        this.$timeout.cancel(this.blurTimer)

        const { focusedBlockIdx } = this.$scope

        // save selection prior to mutation
        if (focusedBlockIdx !== null)
            this.getEditor(focusedBlockIdx).saveSelection()
    }
    toggleBlockWidth() {
        const { focusedBlockIdx, chapter: { blocks } } = this.$scope

        if (focusedBlockIdx !== null) {
            this.updateState()
            blocks[focusedBlockIdx].narrow = !blocks[focusedBlockIdx].narrow
            this.focusBlock(focusedBlockIdx, false, true)
        }
    }
    beforeMoveBlock(up = false) {
        this.$timeout.cancel(this.blurTimer)

        /**
         * Copy selection state from active block to the destination block because in reality we're
         * not moving blocks (editors) around but instead swapping content between them.
         */
        const { focusedBlockIdx } = this.$scope
        if (focusedBlockIdx !== null) {
            const newIdx = focusedBlockIdx + (up ? -1 : 1)
            const fromEditor = this.getEditor(focusedBlockIdx)
            const toEditor = this.getEditor(newIdx)
            const onFocus = this.detachEditorListener(toEditor, 'focus', 'onFocusBlock')

            toEditor.importSelection(fromEditor.exportSelection())
            toEditor.saveSelection()
            toEditor.subscribe('focus', onFocus)
        }
    }
    moveBlock(up = false) {
        const { focusedBlockIdx, chapter: { blocks } } = this.$scope

        if (focusedBlockIdx !== null) {
            const newIdx = focusedBlockIdx + (up ? -1 : 1)

            this.updateState()
            blocks.splice(newIdx, 0, blocks.splice(focusedBlockIdx, 1)[0])
            this.updateEditors()
            this.focusBlock(newIdx, false, true)
        }
    }
    beforeAddMaterial() {
        const editorEl = this.getEditorElements()[this.$scope.focusedBlockIdx]
        
        this.insertHtmlAfterSelection('<div class="material-insertion-marker"></div>')
        
        // @todo Perhaps pass them around rather than cache on gloabl object?
        window.materialInsertionBlockContents = editorEl.innerHTML
        window.materialInsertionBlockIdx = this.$scope.focusedBlockIdx
        
        const marker = editorEl.querySelector('.material-insertion-marker')
        marker.parentNode.removeChild(marker)
    }
    addExistingMaterial() {
        this.$rootScope.savedChapterIndexForMaterialInsertion = this.index
        this.$rootScope.$broadcast(
            window.innerWidth >= BREAK_SM
                ? 'detailedSearch:open'
                : 'mobileSearch:open'
        )
        this.$timeout(() => {
            document.getElementById('header-search-input').focus()
        })
    }
    onInsertExistingMaterials(evt, chapterIdx, selectedMaterials) {
        // @todo Are timeouts necessary?
        if (chapterIdx === this.index)
            this.$timeout(() =>
                this.$timeout(() =>
                    this.insertMaterials(selectedMaterials)
                )
            )
    }
    addNewMaterial() {
        this.$mdDialog.show({
            templateUrl: 'addMaterialDialog.html',
            controller: 'addMaterialDialogController',
            controllerAs: '$ctrl',
            locals: {
                isEditMode: false,
                isAddToPortfolio: true
            }
        }).then(material =>
            this.insertMaterials([material])
        )
    }
    insertMaterials(materials) {
        const editorEl = this.getEditorElements()[window.materialInsertionBlockIdx]
        const materialsHtml = materials.reduce((html, { id }) =>
            html + `<div class="chapter-embed-card chapter-embed-card--material" data-id="${id}"></div>`,
            ''
        )
        editorEl.innerHTML = window.materialInsertionBlockContents.replace('<div class="material-insertion-marker"></div>', materialsHtml)
        
        this.loadEmbeddedContents(editorEl)
        // @todo Restore caret position (put it after inserted materials)
        this.focusBlock(window.materialInsertionBlockIdx)
        delete window.materialInsertionBlockIdx
        delete window.materialInsertionBlockContents
    }
    insertHtmlAfterSelection(html) {
        // courtesy of https://stackoverflow.com/a/6691294
        let sel, range, node

        if (window.getSelection) {
            sel = window.getSelection()

            if (sel.getRangeAt && sel.rangeCount) {
                range = window.getSelection().getRangeAt(0)
                range.collapse(false)

                // Range.createContextualFragment() would be useful here but is
                // non-standard and not supported in all browsers (IE9, for one)
                const el = document.createElement('div')
                const frag = document.createDocumentFragment()

                el.innerHTML = html

                let node, lastNode
                while (node = el.firstChild)
                    lastNode = frag.appendChild(node)

                range.insertNode(frag)
            }
        } else if (document.selection && document.selection.createRange) {
            range = document.selection.createRange()
            range.collapse(false)
            range.pasteHTML(html)
        }
    }
    /**
     * @todo in MS 13: Embed actions
     */
    addMedia() {}
    addRecommendedMaterial() {}
}
controller.$inject = [
    '$scope',
    '$rootScope',
    '$element',
    '$timeout',
    '$translate',
    '$mdDialog',
    'dialogService',
    'iconService',
    'serverCallService',
    'translationService',
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
