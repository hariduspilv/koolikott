/**
 * @todo Evaluate making pull requests to Medium Editor origin or forking it.
 */

{
/**
 * Returns the empty block-level element if caret is positioned on empty WYSIWYG row (and the toolbar
 * is displayed for pre-selecting text format). Otherwise returns undefined.
 */
function getEmptySelectionParent() {
    const getSelectionEditor = (el) => {
        while (el !== null) {
            if (el.classList.contains('medium-editor-element'))
                return el
            el = el.parentElement
        }
    }

    const selection = window.getSelection()

    if (selection.rangeCount) {
        const range = selection.getRangeAt(0)
        let selectionParent = range.commonAncestorContainer

        if (selectionParent.nodeType != Node.ELEMENT_NODE)
            selectionParent = selectionParent.parentNode

        const selectionEditor = getSelectionEditor(selectionParent)

        if (selectionParent &&
            !selectionParent.textContent.trim() &&
            this.base.elements[0] === selectionEditor
        )
            return selectionParent === selectionEditor && selectionParent.firstChild
                ? selectionParent.firstChild
                : selectionParent
    }
}
const isIE = navigator.appName == 'Microsoft Internet Explorer' || !!(
    navigator.userAgent.match(/Trident/) ||
    navigator.userAgent.match(/rv 11/)
)

MediumEditor.extensions.button.prototype.getEmptySelectionParent = getEmptySelectionParent
MediumEditor.extensions.button.prototype.handleClick = function (evt) {
    evt.preventDefault()
    evt.stopPropagation()

    const action = this.getAction()
    if (action) {
        switch (action) {
            case 'bold':
            case 'italic':
                /**
                 * In case BOLD or ITALIC button is hit with empty selection wrap the empty selection
                 * with <b> or <i> or unwrap that element (toggle action).
                 */
                const emptySelectionParent = this.getEmptySelectionParent()
                if (emptySelectionParent) {
                    const tagName = action === 'bold' ? 'B' : 'I'
                    const selection = window.getSelection()

                    if (selection.rangeCount) {
                        const range = selection.getRangeAt(0)
                        const restoreSelection = (emptyEl) => {
                            range.selectNodeContents(emptyEl)
                            selection.removeAllRanges()
                            selection.addRange(range)
                            this.base.getExtensionByName('toolbar').checkState()
                        }
                        const unwrap = (el) => {
                            const parent = el.parentNode

                            while (el.firstChild)
                                parent.insertBefore(el.firstChild, el)

                            parent.removeChild(el)
                            return parent
                        }

                        if (emptySelectionParent.tagName === tagName)
                            restoreSelection(unwrap(emptySelectionParent))
                        else if (emptySelectionParent.parentElement.tagName === tagName) {
                            unwrap(emptySelectionParent.parentElement)
                            restoreSelection(emptySelectionParent)
                        } else {
                            const newNode = document.createElement(tagName)
                            range.surroundContents(newNode)
                            restoreSelection(newNode)
                        }
                        return
                    }
                }
                break
            case 'insertunorderedlist':
                /**
                 * When untoggling a list item it is not automatically wrapped in a <p>.
                 */
                const blockContainer = MediumEditor.util.getTopBlockContainer(MediumEditor.selection.getSelectionStart(document))
                if (blockContainer.tagName === 'UL')
                    MediumEditor.util.execFormatBlock(document, 'p')
        }
        this.execAction(action)
    }
}
/**
 * Display the toolbar in the beginning of a new paragraph wihtout any selection.
 */
const origToolbarCheckState = MediumEditor.extensions.toolbar.prototype.checkState
MediumEditor.extensions.toolbar.prototype.getEmptySelectionParent = getEmptySelectionParent
MediumEditor.extensions.toolbar.prototype.checkState = function () {
    origToolbarCheckState.call(this)

    const emptySelectionParent = this.getEmptySelectionParent()
    if (emptySelectionParent) {
        const selection = window.getSelection()
        if (selection.rangeCount) {
                /**
                 * this is necessary so that the toolbar would be positioned
                 * at the beginning of the empty row.
                 */
                const range = selection.getRangeAt(0)
                range.selectNodeContents(emptySelectionParent)
                selection.removeAllRanges()
                selection.addRange(range)

                // hide link editor
                if (!this.isToolbarDefaultActionsDisplayed())
                    this.showToolbarDefaultActions()

                this.showToolbar()
                this.setToolbarButtonStates()
                this.positionToolbar(window.getSelection())

                // Without this FF won't let the cursor be moved off the empty row with keyboard arrows
                range.collapse(true)
        }
    }
}
if (isIE) {
    MediumEditor.extensions.toolbar.prototype.handleBlur = function () {
        clearTimeout(this.hideTimeout)
        clearTimeout(this.delayShowTimeout)
        this.hideTimeout = setTimeout(() => {
            this.preventBlurClose
                ? this.preventBlurClose = false
                : this.hideToolbar()
        }, 1)
    }
    MediumEditor.extensions.toolbar.prototype.createToolbarButtons = function () {
        const ul = this.document.createElement('ul')
        ul.id = 'medium-editor-toolbar-actions' + this.getEditorId()
        ul.className = 'medium-editor-toolbar-actions'
        ul.style.display = 'block'

        this.buttons.forEach(button => {
            const buttonName = typeof button === 'string' ? button : button.name
            const buttonOpts = typeof button === 'string' ? null : button
            const extension = this.base.addBuiltInExtension(buttonName, buttonOpts)

            if (extension && typeof extension.getButton === 'function') {
                const btn = extension.getButton(this.base)
                const li = this.document.createElement('li')

                li.addEventListener('mousedown', () =>
                    this.preventBlurClose = true
                )
                MediumEditor.util.isElement(btn)
                    ? li.appendChild(btn)
                    : li.innerHTML = btn

                ul.appendChild(li)
            }
        })

        const buttons = ul.querySelectorAll('button')
        if (buttons.length > 0) {
            buttons[0].classList.add(this.firstButtonClass)
            buttons[buttons.length - 1].classList.add(this.lastButtonClass)
        }

        return ul
    }
}
MediumEditor.extensions.placeholder.prototype.showPlaceholder = (el) => {
    if (el) {
        el.classList.add('medium-editor-placeholder')
        el.classList.remove('medium-editor-placeholder-relative')
    }
}
MediumEditor.extensions.anchorPreview.prototype.handleEditableMouseover = function (event) {
    const target = MediumEditor.util.getClosestTag(event.target, 'a')
    const isEmbedElement = (el) => {
        while (el !== null) {
            if (el.classList.contains('chapter-embed-card') ||
                el.classList.contains('embed-item') ||
                el.classList.contains('embed-responsive-container') ||
                el.classList.contains('embedded-material')
            )
                return true
            if (el.classList.contains('chapter-block'))
                return false
            el = el.parentElement
        }
    }

    if (target) {
        if (isEmbedElement(target))
            return true

        if (!this.showOnEmptyLinks && (
            !/href=["']\S+["']/.test(target.outerHTML) ||
            /href=["']#\S+["']/.test(target.outerHTML)
        ))
            return true

        const toolbar = this.base.getExtensionByName('toolbar');
        if (!this.showWhenToolbarIsVisible &&
            toolbar &&
            toolbar.isDisplayed &&
            toolbar.isDisplayed()
        )
            return true

        if (this.activeAnchor && this.activeAnchor !== target)
            this.detachPreviewHandlers()

        this.anchorToPreview = target
        this.instanceHandleAnchorMouseout = this.handleAnchorMouseout.bind(this)
        this.on(this.anchorToPreview, 'mouseout', this.instanceHandleAnchorMouseout)
        this.base.delay(() => {
            if (this.anchorToPreview)
                this.showPreview(this.anchorToPreview)
        })
    }
}
/**
 * Overwriting Medium Editor's util methods to suit our needs.
 * The original method accepts a blacklist of attributes that should be removed on all elements
 * while our method accepts a whilelist object mapping which attributes are allowed on which elements.
 * Original method is in source code at:
 * https://github.com/yabwe/medium-editor/blob/c619e756c0c8dda7bbb587b6b97209d984c208e8/src/js/util.js#L1037-L1041
 */
MediumEditor.util.cleanupAttrs = (el, blacklist) => {
    const allowed = EDITOR_ALLOWED_TAGS_AND_ATTRIBUTES[el.tagName]

    for (let { name } of el.attributes)
        if (!allowed || allowed.indexOf(name.toLowerCase()) < 0)
            el.removeAttribute(name)
}
/**
 * The original method unwraps elements that are specified in the provided blacklist (options.unwrapTags)
 * while ours unwraps all elements that are not in our allowed whitelist.
 * Original method is in source code at:
 * https://github.com/yabwe/medium-editor/blob/c619e756c0c8dda7bbb587b6b97209d984c208e8/src/js/util.js#L1049-L1053
 */
MediumEditor.util.unwrapTags = (el, blacklist) => {
    // convert other healines to H3
    if (['H1', 'H2', 'H4', 'H5', 'H6'].indexOf(el.nodeName) > -1)
        el.outerHTML = `<h3>${el.innerHTML}</h3>`
    else
    // unwrap DIVs that are not embeds & all other tags
    if ((el.nodeType === Node.ELEMENT_NODE && el.nodeName === 'DIV' && !el.classList.contains('chapter-embed-card')) ||
        (el.parentNode && EDITOR_ALLOWED_TAGS.indexOf(el.nodeName) < 0)
    )
        MediumEditor.util.unwrap(el, document)
}
/**
 * Need to patch this and add the condition: blockContainer.nodeName !== 'UL'. Without it creating
 * blockquote from list item was broken. See Medium Editor source code for more inline comments:
 * https://github.com/yabwe/medium-editor/blob/master/src/js/util.js#L512-L573
 */
MediumEditor.util.execFormatBlock = function (doc, tagName) {
    const blockContainer = this.getTopBlockContainer(MediumEditor.selection.getSelectionStart(doc))

    if (tagName === 'blockquote') {
        if (blockContainer) {
            if (blockContainer.nodeName === 'UL') {
                // refresh the tollbar state
                setTimeout(() => {
                    const edirorEl = this.getContainerEditorElement(blockContainer)
                    const editor = edirorEl && MediumEditor.getEditorFromElement(edirorEl)
                    if (editor)
                        editor.getExtensionByName('toolbar').checkState()
                }, 100)
            } else
                for (let childNode of blockContainer.childNodes)
                    if (this.isBlockContainer(childNode))
                        return doc.execCommand('outdent', false, null)
        }

        if (this.isIE)
            return doc.execCommand('indent', false, tagName)
    }

    if (blockContainer && tagName === blockContainer.nodeName.toLowerCase())
        tagName = 'p'

    if (this.isIE)
        tagName = '<' + tagName + '>'

    if (blockContainer && blockContainer.nodeName.toLowerCase() === 'blockquote') {
        if (this.isIE && tagName === '<p>')
            return doc.execCommand('outdent', false, tagName)

        if ((this.isFF || this.isEdge) && tagName === 'p') {
            for (let childNode of blockContainer.childNodes)
                if (!this.isBlockContainer(childNode))
                    doc.execCommand('formatBlock', false, tagName)

            return doc.execCommand('outdent', false, tagName)
        }
    }

    return doc.execCommand('formatBlock', false, tagName)
}
}