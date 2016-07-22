# -*- coding: utf-8 -*-
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import Select
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support.select import Select
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.remote import switch_to
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.common.desired_capabilities import DesiredCapabilities
import unittest, time, re
import os
import random
import string


"""
Tutorial:

Meetodid:
    "Mine_" - liigub lehtede vahet
    "Lisa_" - t2idab infot
    "Otsi_" - kasutab otsingut mingite kriteeriumite p6hjal
    "Vali_" - vahetab kasutajat (vajab enne minekut osapoole kasutaja lehele)
    "K2ivita_" - nupuvahetus lehel, enamasti avab popupi või teeb mingit tegevust piopupis
    "Logi_Sisse" - erandlik
    Struktuur:
	Tee_kellega_kus_mida: Lisa_T2iturina_Toimikus_yks_sissen6udja_aadress
"""

def random_Number(asd):
    SuvaString = ''.join(random.choice(string.digits) for x in range(asd))
    return SuvaString

class DOP(unittest.TestCase):
    
    def setUp(self):
        #remote driver
        self.driver = webdriver.Remote(
	        command_executor = 'http://127.0.0.1:4444/wd/hub',
	        #desired_capabilities = DesiredCapabilities.CHROME)
	        desired_capabilities = DesiredCapabilities.FIREFOX)
            #desired_capabilities = DesiredCapabilities.PHANTOMJS)
            #desired_capabilities = DesiredCapabilities.INTERNETEXPLORER)
        self.driver.implicitly_wait(30)
        self.base_url = "http://oxygen.netgroupdigital.com/"
        self.verificationErrors = []
        self.accept_next_alert = True

    def GoToSearchResult(self):
        driver = self.driver
        wait = WebDriverWait(driver,3)
        
        driver.find_element_by_xpath("//a[contains(@data-ng-href), 'materialId=1']").click()

    def QuickSearchFor(self, search):
        driver = self.driver
        wait = WebDriverWait(driver,3)
        driver.find_element_by_css_selector(".form-control.ng-pristine.ng-untouched.ng-valid").send_keys(search)
        driver.find_element_by_css_selector(".btn.btn-default").click()

    def GoToFirstPage(self):
        driver = self.driver
        wait = WebDriverWait(driver,3)
        driver.get(self.base_url)



    #TESTS
    def test_mainFlow(self):
        DOP.GoToFirstPage(self)
        time.sleep(10)
        DOP.QuickSearchFor(self, "matemaatika")
        time.sleep(10)



    
    def is_element_present(self, how, what):
        try: self.driver.find_element(by=how, value=what)
        except NoSuchElementException, e: return False
        return True
    
    def is_alert_present(self):
        try: self.driver.switch_to_alert()
        except NoAlertPresentException, e: return False
        return True
    
    def close_alert_and_get_its_text(self):
        try:
            alert = self.driver.switch_to_alert()
            alert_text = alert.text
            if self.accept_next_alert:
                alert.accept()
            else:
                alert.dismiss()
            return alert_text
        finally: self.accept_next_alert = True
    
    def tearDown(self):
        self.driver.quit()
        self.assertEqual([], self.verificationErrors)

if __name__ == "__main__":
    unittest.main()