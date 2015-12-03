define(['app'], function(app)
{
	app.directive('dopSidebar', ['translationService', '$location',
	function(translationService, $location) {
		return {
			scope: true,
			templateUrl: 'directives/sidebar/sidebar.html',
			controller: function($scope) {
				$scope.recommendations = [{
					what: 'Geograafia õpik gümnaasiumile, III kursus. Maailma ühiskonnageograafia. Loodusvarade majandamine ja keskkonnaprobleemid',
					who: 'Sulev Mäeltsemees',
					when: '2015',
					kind: 'book',
					notes: " III kursuse geograafiaõpik gümnaasiumile vaatleb "
				}, {
					what: 'Eesti ajaloo õpik gümnaasiumile, II osa. Rootsi ajast 20. saj alguseni',
					who: 'Pärtel Piirimäe, Andres Andresen, Marten Seppel, Ago Pajur',
					when: '2015',
					kind: 'book',
					notes: "16. sajandi teine pool ning 17. sajandi esimene pool oli kogu ",
					image: "http://www.avita.ee/wtfiles/0/63848e77abca0f2873fb4f648035831a.jpg"
				}, {
					what: 'Ajaloo õpik 9. klassile. Lähiajalugu, I osa',
					who: 'Einar Värä, Ago Pajur, Tõnu Tannberg',
					when: '2015',
					kind: 'audiotrack',
					notes: "9. klassi uue Lähiajaloo õpiku kaheks tähtsamaks raskuspunktiks on ideede ajalugu ja inimesekeskne ajalugu."
				}, {
					what: 'Füüsika 9. klassile. Soojusõpetus. Tuumaenergia',
					who: 'Enn Pärtel, Jaak Lõhmus, Rein-Karl Loide',
					when: '2015',
					kind: 'videocam',
					notes: "Uue kujundusega õpik arvestab 2010. a ainekava nõudeid. Aine ehituse mudel. Soojusliikumine. Celsiuse temperatuuriskaala. Soojusülekanne (soojusülekande liigid, rakendused, aastaaegade vaheldumine, ainete soojuslikud kar"
				}, {
					what: 'Keemia õpik 8. klassile',
					who: 'Taavi Ivan',
					when: '2015',
					kind: 'audiotrack',
					notes: "Uue põhikooli VIII klassi õpiku peamised ülesanded on äratada õpilastes"
				},
				{
					what: 'Normaalne söömine',
					who: 'Mihkel Zilmer, Urmas Kokassaar, Anne Lill',
					when: '2015',
					kind: 'audiotrack',
					notes: "Söömine on inimese elu alus. Puudujäägid ja liialdused ",
					image: "http://www.avita.ee/wtfiles/0/63848e77abca0f2873fb4f648035831a.jpg"
				}];
			}
		}
	}]
);

return app;
});
