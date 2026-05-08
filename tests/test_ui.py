import pytest
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


DEFAULT_TIMEOUT = 15


def open_home(driver, base_url):
    driver.get(base_url)


def wait_for_visible(driver, by, selector, timeout=DEFAULT_TIMEOUT):
    return WebDriverWait(driver, timeout).until(
        EC.visibility_of_element_located((by, selector))
    )


def wait_for_all(driver, by, selector, timeout=DEFAULT_TIMEOUT):
    return WebDriverWait(driver, timeout).until(
        EC.presence_of_all_elements_located((by, selector))
    )


def get_food_items(driver):
    return wait_for_all(driver, By.CSS_SELECTOR, ".food-item")


def add_one_item_to_cart(driver):
    items = get_food_items(driver)
    if not items:
        pytest.skip("No food items available to add to cart.")
    first = items[0]
    add_button = first.find_element(By.CSS_SELECTOR, "img.add")
    add_button.click()
    WebDriverWait(driver, DEFAULT_TIMEOUT).until(
        lambda d: len(first.find_elements(By.CSS_SELECTOR, ".food-item-counter")) > 0
    )
    return first


def test_homepage_loads_header_text(driver, base_url):
    open_home(driver, base_url)
    header = wait_for_visible(driver, By.CSS_SELECTOR, ".header-contents h2")
    assert "Order your favourite food here" in header.text


def test_header_view_menu_button_text(driver, base_url):
    open_home(driver, base_url)
    button = wait_for_visible(driver, By.CSS_SELECTOR, ".header-contents button")
    assert button.text.strip() == "View Menu"


def test_navbar_menu_links_present(driver, base_url):
    open_home(driver, base_url)
    menu_links = wait_for_all(driver, By.CSS_SELECTOR, ".navbar-menu a")
    link_text = [link.text.strip().lower() for link in menu_links]
    assert "home" in link_text
    assert "menu" in link_text
    assert "mobile-app" in link_text
    assert "contact us" in link_text


def test_navbar_sign_in_button_present(driver, base_url):
    open_home(driver, base_url)
    button = wait_for_visible(driver, By.CSS_SELECTOR, ".navbar-right button")
    assert button.text.strip() == "sign in"


def test_explore_menu_section_visible(driver, base_url):
    open_home(driver, base_url)
    section = wait_for_visible(driver, By.CSS_SELECTOR, "#explore-menu h1")
    assert "Explore our menu" in section.text


def test_app_download_section_visible(driver, base_url):
    open_home(driver, base_url)
    section = wait_for_visible(driver, By.CSS_SELECTOR, "#app-download")
    assert "Tomato App" in section.text


def test_footer_section_visible(driver, base_url):
    open_home(driver, base_url)
    footer = wait_for_visible(driver, By.CSS_SELECTOR, "#footer")
    assert "Get in touch" in footer.text


def test_food_items_loaded(driver, base_url):
    open_home(driver, base_url)
    items = get_food_items(driver)
    assert len(items) > 0


def test_add_to_cart_shows_counter(driver, base_url):
    open_home(driver, base_url)
    first = add_one_item_to_cart(driver)
    counter = first.find_element(By.CSS_SELECTOR, ".food-item-counter p")
    assert counter.text.strip() == "1"


def test_add_to_cart_increases_quantity(driver, base_url):
    open_home(driver, base_url)
    first = add_one_item_to_cart(driver)
    buttons = first.find_elements(By.CSS_SELECTOR, ".food-item-counter img")
    buttons[-1].click()
    WebDriverWait(driver, DEFAULT_TIMEOUT).until(
        lambda d: first.find_element(By.CSS_SELECTOR, ".food-item-counter p").text.strip() == "2"
    )


def test_cart_dot_shows_when_cart_has_items(driver, base_url):
    open_home(driver, base_url)
    add_one_item_to_cart(driver)
    dot = wait_for_visible(driver, By.CSS_SELECTOR, ".navbar-search-icon .dot")
    assert dot is not None


def test_navigate_to_cart_page(driver, base_url):
    open_home(driver, base_url)
    add_one_item_to_cart(driver)
    cart_link = wait_for_visible(driver, By.CSS_SELECTOR, "a[href='/cart']")
    cart_link.click()
    WebDriverWait(driver, DEFAULT_TIMEOUT).until(EC.url_contains("/cart"))
    wait_for_visible(driver, By.CSS_SELECTOR, ".cart")


def test_cart_totals_section_present(driver, base_url):
    open_home(driver, base_url)
    add_one_item_to_cart(driver)
    driver.get(base_url + "/cart")
    totals = wait_for_visible(driver, By.CSS_SELECTOR, ".cart-total h2")
    assert "Cart Totals" in totals.text


def test_cart_checkout_button_text(driver, base_url):
    open_home(driver, base_url)
    add_one_item_to_cart(driver)
    driver.get(base_url + "/cart")
    checkout_button = wait_for_visible(driver, By.CSS_SELECTOR, ".cart-total button")
    assert checkout_button.text.strip() == "PROCEED TO CHECKOUT"


def test_promocode_input_present(driver, base_url):
    open_home(driver, base_url)
    add_one_item_to_cart(driver)
    driver.get(base_url + "/cart")
    promo = wait_for_visible(driver, By.CSS_SELECTOR, ".cart-promocode-input input")
    assert promo.get_attribute("placeholder").lower() == "promo code"


def test_promocode_submit_button_text(driver, base_url):
    open_home(driver, base_url)
    add_one_item_to_cart(driver)
    driver.get(base_url + "/cart")
    submit_button = wait_for_visible(driver, By.CSS_SELECTOR, ".cart-promocode-input button")
    assert submit_button.text.strip() == "Submit"


def test_remove_item_from_cart(driver, base_url):
    open_home(driver, base_url)
    add_one_item_to_cart(driver)
    driver.get(base_url + "/cart")
    items = wait_for_all(driver, By.CSS_SELECTOR, ".cart-items-item")
    assert len(items) > 0
    remove_button = items[0].find_element(By.CSS_SELECTOR, ".cross")
    remove_button.click()
    WebDriverWait(driver, DEFAULT_TIMEOUT).until(
        lambda d: len(d.find_elements(By.CSS_SELECTOR, ".cart-items-item")) == 0
    )


def test_checkout_without_login_redirects_to_cart(driver, base_url):
    open_home(driver, base_url)
    add_one_item_to_cart(driver)
    driver.get(base_url + "/cart")
    checkout_button = wait_for_visible(driver, By.CSS_SELECTOR, ".cart-total button")
    checkout_button.click()
    WebDriverWait(driver, DEFAULT_TIMEOUT).until(EC.url_contains("/cart"))


def test_login_popup_open_and_close(driver, base_url):
    open_home(driver, base_url)
    sign_in = wait_for_visible(driver, By.CSS_SELECTOR, ".navbar-right button")
    sign_in.click()
    popup = wait_for_visible(driver, By.CSS_SELECTOR, ".login-popup")
    assert popup is not None
    action_button = wait_for_visible(driver, By.CSS_SELECTOR, ".login-popup-container button")
    assert action_button.text.strip() == "Login"
    close_icon = wait_for_visible(driver, By.CSS_SELECTOR, ".login-popup-title img")
    close_icon.click()
    WebDriverWait(driver, DEFAULT_TIMEOUT).until(
        EC.invisibility_of_element_located((By.CSS_SELECTOR, ".login-popup"))
    )


def test_login_popup_toggle_signup_shows_name(driver, base_url):
    open_home(driver, base_url)
    sign_in = wait_for_visible(driver, By.CSS_SELECTOR, ".navbar-right button")
    sign_in.click()
    toggle = wait_for_visible(driver, By.XPATH, "//span[contains(text(),'Click here')]")
    toggle.click()
    name_input = wait_for_visible(driver, By.CSS_SELECTOR, "input[name='name']")
    assert name_input is not None
    action_button = wait_for_visible(driver, By.CSS_SELECTOR, ".login-popup-container button")
    assert action_button.text.strip() == "Create Account"
