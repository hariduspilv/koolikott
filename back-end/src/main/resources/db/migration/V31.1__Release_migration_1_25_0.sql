SET foreign_key_checks = 0;

update Translation set translation = 'CC BY: Autorile viitamine' where translationKey = 'LICENSETYPE_LONG_NAME_CCBY' and translationGroup = 1;
update Translation set translation = 'CC BY: Атрибуция' where translationKey = 'LICENSETYPE_LONG_NAME_CCBY' and translationGroup = 2;
update Translation set translation = 'CC BY: Attribution' where translationKey = 'LICENSETYPE_LONG_NAME_CCBY' and translationGroup = 3;
update Translation set translation = 'CC BY-NC: Autorile viitamine, mitteäriline eesmärk' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYNC' and translationGroup = 1;
update Translation set translation = 'CC BY-NC: Атрибуция - Некоммерческая' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYNC' and translationGroup = 2;
update Translation set translation = 'CC BY-NC: Attribution-NonCommercial' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYNC' and translationGroup = 3;
update Translation set translation = 'CC BY-NC-ND: Autorile viitamine, mitteäriline eesmärk, tuletatud teoste keeld' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYNCND' and translationGroup = 1;
update Translation set translation = 'CC BY-NC-ND: Атрибуция - Некоммерческая - Без Производных' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYNCND' and translationGroup = 2;
update Translation set translation = 'CC BY-NC-ND: Attribution-NonCommercial-NoDerivatives' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYNCND' and translationGroup = 3;
update Translation set translation = 'CC BY-NC-SA: Autorile viitamine, mitteäriline eesmärk, jagamine samadel tingimustel' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYNCSA' and translationGroup = 1;
update Translation set translation = 'CC BY-NC-SA: Атрибуция - Некоммерческая - С сохранением условий' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYNCSA' and translationGroup = 2;
update Translation set translation = 'CC BY-NC-SA: Attribution-NonCommercial-ShareAlike' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYNCSA' and translationGroup = 3;
update Translation set translation = 'CC BY-ND: Autorile viitamine, tuletatud teoste keeld' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYND' and translationGroup = 1;
update Translation set translation = 'CC BY-ND: Атрибуция - Без Производных Произведений' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYND' and translationGroup = 2;
update Translation set translation = 'CC BY-ND: Attribution-NoDerivatives' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYND' and translationGroup = 3;
update Translation set translation = 'CC BY-SA: Autorile viitamine, jagamine samadel tingimustel' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYSA' and translationGroup = 1;
update Translation set translation = 'CC BY-SA: Атрибуция - С сохранением условий' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYSA' and translationGroup = 2;
update Translation set translation = 'CC BY-SA: Attribution-ShareAlike' where translationKey = 'LICENSETYPE_LONG_NAME_CCBYSA' and translationGroup = 3;

SET foreign_key_checks = 1;