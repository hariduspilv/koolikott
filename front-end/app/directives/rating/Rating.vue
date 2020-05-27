<template>
    <div class="rating" layout="row" layout-align="space-between center" flex>
        <div class="rating__item md-caption">
            <md-tooltip md-direction="top">Vaatamiste arv</md-tooltip>
            <md-icon class="rating__icon">remove_red_eye</md-icon>
            <span v-if="material" class="rating__number">{{material.views}}</span>
            <span v-if="portfolio" class="rating__number">{{portfolio.views}}</span>
        </div>
    </div>
</template>

<script>
    export default {
        props: ['material', 'portfolio'],
        methods: {

        }
    }
</script>


<style lang="css" scoped>

</style>
