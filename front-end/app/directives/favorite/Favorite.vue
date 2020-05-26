<template>
    <div class="md-icon-button md-button favorite" v-on:click="toggleFavorite($event)">
        <!--<v-tooltip md-direction="bottom">
            <span v-if="!hasFavorited">Lisa lemmikuks</span>
            <span v-if="hasFavorited">Eelmalda lemmikutest</span>
        </v-tooltip>-->
STAR
       <!-- <v-icon
                class="favorite__icon"
                v-if="!hasFavorited && (isEditPortfolioMode || isViewMaterialOrPortfolioPage || hover)"
                v-on:click="favorite($event)"
        >star_outline</v-icon>-->
<!--        <v-icon class="favorite__icon" v-if="hasFavorited" v-on:click="removeFavorite($event)">star</v-icon>-->
    </div>
</template>

<script>

    import VueAuthenticatedUserService from '../../services/vueAuthenticatedUser.service';
    import VueServerCallService from '../../services/vueServerCall.service';
    import VueCommons from "../../utils/vueCommons";

    export default {
        name: "Favorite",
        props: ['learningObject', 'hover'],
        data: function () {
            return {
                hasFavorited: true,
                isEditPortfolioMode: null,
                isViewMaterialOrPortfolioPage: null,
            }
        },
        methods: {
            getFavoriteData: function() {
                if (this.learningObject && VueAuthenticatedUserService.isAuthenticated()) {
                    if (this.learningObject.favorite) {
                        this.hasFavorited = true;
                    } else if (this.learningObject.favorite == null) {
                        VueServerCallService.makeGet('rest/learningObject/favorite?id=' + this.learningObject.id)
                            .then((response) => {
                                if (response.data && response.data.id) {
                                    this.hasFavorited = true
                                }
                            })
                            .catch((error) => {console.log(error)})
                    }
                }
            },
            postFavorite: function({id, type}) {

                VueServerCallService.makePost('rest/learningObject/favorite', {id, type})
                    .then((response) => {
                        if (200 <= response.status && response.status < 300) {
                            console.log('ADDED TO FAVORITES');
                            this.hasFavorited = true
                        }
                    }).catch(() => this.hasFavorited = false)
            },

            favorite: function($event) {
                $event.preventDefault();
                $event.stopPropagation();

                console.log('Posting favorite');
                if (this.VueAuthenticatedUserService.isAuthenticated()) {
                    VueCommons.isPortfolio(this.learningObject.type)
                        ? VueCommons.getPortfolioById(this.learningObject.id).then(this.postFavorite)
                        : VueCommons.getMaterialById(this.learningObject.id).then(this.postFavorite);

                    this.hasFavorited = true;
                }
            },
            removeFavorite: function($event) {
                $event.preventDefault();
                $event.stopPropagation();

                console.log('Removing favorite');
                const { id } = this.learningObject;

                if (this.hasFavorited && VueAuthenticatedUserService.isAuthenticated()) {
                    VueServerCallService.makeDelete('rest/learningObject/favorite?id=' + id)
                    .then(({status}) => {
                        if (200 <= status && status < 300) {
                            console.log('REMOVED_FROM_FAVORITES');
                            this.hasFavorited = false
                        }
                    }, (error) => {
                        console.log(error)
                        this.hasFavorited = true
                    });

                    this.hasFavorited = false;
                }
            },
            toggleFavorite: function ($event) {
                if (this.hasFavorited) this.removeFavorite($event);
                // if (!this.hasFavorited) this.favorite($event)
            }
        },
        beforeUpdate() {
            this.getFavoriteData()
        },
        watch: {
            learningObject: function (current, previous) {
                if (this.learningObject && current !== previous) {
                    this.getFavoriteData()
                }
            }
        }
    }
</script>

<style scoped>

</style>
