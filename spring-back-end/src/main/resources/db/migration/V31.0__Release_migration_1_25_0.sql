SET foreign_key_checks = 0;

update Translation set translation = 'BY: Autorile viitamine' where translationKey = 'LICENSETYPE_LONG_NAME_CCBY' and translationGroup = 1;
update Translation set translation = 'BY: Атрибуция' where translationKey = 'LICENSETYPE_LONG_NAME_CCBY' and translationGroup = 2;
update Translation set translation = 'BY: Attribution' where translationKey = 'LICENSETYPE_LONG_NAME_CCBY' and translationGroup = 3;
update Translation set translation = 'BYNC: Autorile viitamine, mitteäriline eesmärk' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYNC' and translationGroup = 1;
update Translation set translation = 'BYNC: Атрибуция - Некоммерческая' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYNC' and translationGroup = 2;
update Translation set translation = 'BYNC: Attribution-NonCommercial' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYNC' and translationGroup = 3;
update Translation set translation = 'BYNCND: Autorile viitamine, mitteäriline eesmärk, tuletatud teoste keeld' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYNCND' and translationGroup = 1;
update Translation set translation = 'BYNCND: Атрибуция - Некоммерческая - Без Производных' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYNCND' and translationGroup = 2;
update Translation set translation = 'BYNCND: Attribution-NonCommercial-NoDerivatives' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYNCND' and translationGroup = 3;
update Translation set translation = 'BYNCSA: Autorile viitamine, mitteäriline eesmärk, jagamine samadel tingimustel' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYNCSA' and translationGroup = 1;
update Translation set translation = 'BYNCSA: Атрибуция - Некоммерческая - С сохранением условий' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYNCSA' and translationGroup = 2;
update Translation set translation = 'BYNCSA: Attribution-NonCommercial-ShareAlike' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYNCSA' and translationGroup = 3;
update Translation set translation = 'BYND: Autorile viitamine, tuletatud teoste keeld' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYND' and translationGroup = 1;
update Translation set translation = 'BYND: Атрибуция - Без Производных Произведений' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYND' and translationGroup = 2;
update Translation set translation = 'BYND: Attribution-NoDerivatives' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYND' and translationGroup = 3;
update Translation set translation = 'BYSA: Autorile viitamine, jagamine samadel tingimustel' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYSA' and translationGroup = 1;
update Translation set translation = 'BYSA: Атрибуция - С сохранением условий' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYSA' and translationGroup = 2;
update Translation set translation = 'BYSA: Attribution-ShareAlike' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYSA' and translationGroup = 3;

SET foreign_key_checks = 1;