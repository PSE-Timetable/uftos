import { expect, test, type Page } from '@playwright/test';

let page: Page;

test.describe.configure({ mode: 'serial' });

//tests need to be done in order or they might break!
test.describe('Students page', () => {
  test.beforeAll('delete all existing students', async ({ browser }) => {
    page = await browser.newPage();
    await page.goto('http://localhost:5173/');
    await page.getByRole('link').first().click();
    await page.locator('div:nth-child(2) > .bg-accent').first().click();
    await page.waitForTimeout(500); //not ideal, but helps to load the page fully

    while (
      (await page.getByRole('cell', { name: 'Open menu' }).first().isVisible()) &&
      (await page.getByRole('row', { name: 'Vorname Nachname Tags' }).getByRole('checkbox').isVisible())
    ) {
      await page.getByRole('row', { name: 'Vorname Nachname Tags' }).getByRole('checkbox').check();
      await page.keyboard.press('Delete');
      await page.getByRole('button', { name: 'Löschen' }).click();
      await page.waitForTimeout(100);
    }
  });

  test('create entities', async () => {
    await expect(page.locator('body')).toContainText('Hinzufügen');
    await page.getByRole('button', { name: 'Hinzufügen' }).click();
    await page.getByRole('textbox').first().click();
    await page.getByRole('textbox').first().fill('odgsogdpnpowad');
    await page.getByRole('textbox').nth(1).click();
    await page.getByRole('textbox').nth(1).fill('pdhfgfönwßemü');
    await page.getByRole('button', { name: 'Speichern' }).click();
    await expect(page.locator('tbody')).toContainText('odgsogdpnpowad');
    await expect(page.locator('tbody')).toContainText('pdhfgfönwßemü');
    await page.getByRole('button', { name: 'Hinzufügen' }).click();
    await page.getByRole('textbox').first().click();
    await page.getByRole('textbox').first().fill('test1');
    await page.getByRole('textbox').nth(1).click();
    await page.getByRole('textbox').nth(1).fill('test35');
    await page.getByRole('button', { name: 'Speichern' }).click();
    await page.getByRole('button', { name: 'Hinzufügen' }).click();
    await page.getByRole('textbox').first().click();
    await page.getByRole('textbox').first().fill('a');
    await page.getByRole('textbox').nth(1).click();
    await page.getByRole('textbox').nth(1).fill('b');
    await page.getByRole('button', { name: 'Speichern' }).click();
    await page.getByRole('button', { name: 'Vorname' }).click();
  });

  test('check table', async () => {
    await expect(page.getByRole('row')).toHaveCount(4); // header counts as row
    await expect(page.locator('tbody')).toContainText('a');
    await expect(page.locator('tbody')).toContainText('odgsogdpnpowad');
    await expect(page.locator('tbody')).toContainText('test1');
    await expect(page.locator('tbody')).toContainText('b');
    await expect(page.locator('tbody')).toContainText('pdhfgfönwßemü');
    await expect(page.locator('tbody')).toContainText('test35');
  });

  test('search', async () => {
    await page.getByPlaceholder('Suche...').click();
    await page.getByPlaceholder('Suche...').fill('b');
    await expect(page.getByRole('row')).toHaveCount(2);
    await page.getByPlaceholder('Suche...').click();
    await page.getByPlaceholder('Suche...').fill('odgsogdpnpowad pdhfgfönwßemü');
    await expect(page.getByRole('row')).toHaveCount(2);
    await page.getByPlaceholder('Suche...').fill('');
    await expect(page.getByRole('row')).toHaveCount(4);
  });

  test('edit entity', async () => {
    await page.getByRole('row', { name: 'a b Open menu' }).getByRole('button').click();
    await page.getByRole('menuitem', { name: 'Editieren' }).click();
    await expect(page.getByRole('textbox').first()).toHaveValue('a');
    await expect(page.getByRole('textbox').nth(1)).toHaveValue('b');
    await page.getByRole('textbox').first().click();
    await page.getByRole('textbox').first().fill('atsfws');
    await page.getByRole('button', { name: 'Speichern' }).click();
    await expect(page.locator('tbody')).toContainText('atsfws');
  });

  test('select and delete', async () => {
    await expect(page.locator('body')).toContainText('0 von 3 Zeile(n) ausgewählt.');
    await page.getByRole('row', { name: 'test1 test35 Open menu' }).getByRole('checkbox').check();
    await expect(page.locator('body')).toContainText('1 von 3 Zeile(n) ausgewählt.');
    await page.getByRole('row', { name: 'odgsogdpnpowad pdhfgfönwßemü Open menu' }).getByRole('button').click();
    await page.getByRole('menuitem', { name: 'Löschen' }).click();
    await expect(page.getByRole('row')).toHaveCount(3);
    await expect(page.locator('body')).toContainText('1 von 2 Zeile(n) ausgewählt.');
    await page.keyboard.press('Delete');
    await page.getByRole('button', { name: 'Löschen' }).click();
    await expect(page.getByRole('row')).toHaveCount(2);
    await expect(page.locator('body')).toContainText('0 von 1 Zeile(n) ausgewählt.');
    await page.getByRole('row', { name: 'Vorname Nachname Tags' }).getByRole('checkbox').check();
    await page.keyboard.press('Delete');
    await page.getByRole('button', { name: 'Löschen' }).click();
  });

  test('pagination', async () => {
    for (let i = 0; i < 25; i++) {
      await expect(page.locator('body')).toContainText('Hinzufügen');
      await page.getByRole('button', { name: 'Hinzufügen' }).click();
      await page.getByRole('textbox').first().click();
      await page.getByRole('textbox').first().fill(`Max${i}`);
      await page.getByRole('textbox').nth(1).click();
      await page.getByRole('textbox').nth(1).fill(`Mustermann${i}`);
      await page.getByRole('button', { name: 'Speichern' }).click();
    }
    await expect(page.getByRole('row')).toHaveCount(16);
    await expect(page.locator('body')).toContainText('0 von 25 Zeile(n) ausgewählt.');
    await expect(page.getByLabel('Previous')).toBeDisabled();
    await expect(page.getByLabel('Page 1')).toHaveCSS('border-color', 'rgb(43, 109, 136)');
    await expect(page.getByLabel('Page 2')).not.toHaveCSS('border-color', 'rgb(43, 109, 136)');
    await page.getByLabel('Next').click();
    await expect(page.getByLabel('Previous')).toBeEnabled();
    await expect(page.getByLabel('Next')).toBeDisabled();
    await expect(page.getByLabel('Page 1')).not.toHaveCSS('border-color', 'rgb(43, 109, 136)');
    await expect(page.getByLabel('Page 2')).toHaveCSS('border-color', 'rgb(43, 109, 136)');
    await expect(page.getByRole('row')).toHaveCount(11);
    await page.getByPlaceholder('Suche...').fill('Max1 Mustermann1');
    await expect(page.getByRole('row')).toHaveCount(2);
    await expect(page.getByLabel('Previous')).toBeDisabled();
    await expect(page.getByLabel('Next')).toBeDisabled();
    await expect(page.getByLabel('Page 1')).toHaveCSS('border-color', 'rgb(43, 109, 136)');
    await expect(page.getByLabel('Page 2')).toBeHidden();
    await page.getByPlaceholder('Suche...').fill('');
    await expect(page.getByLabel('Previous')).toBeDisabled();
    await expect(page.getByLabel('Page 1')).toHaveCSS('border-color', 'rgb(43, 109, 136)');
    await expect(page.getByLabel('Page 2')).not.toHaveCSS('border-color', 'rgb(43, 109, 136)');
    await expect(page.getByLabel('Next')).toBeEnabled();
  });

  test('sorting', async () => {
    await page.getByRole('button', { name: 'Vorname' }).click();
    const firstNames = [];
    for (let i = 0; i < 25; i++) {
      firstNames.push(`Max${i}`);
    }
    firstNames.sort();

    for (let i = 1; i <= 15; i++) {
      await expect(page.getByRole('row').nth(i).getByRole('cell').nth(1)).toHaveText(firstNames[i - 1]);
    }

    await page.getByLabel('Next').click();
    for (let i = 1; i <= 10; i++) {
      await expect(page.getByRole('row').nth(i).getByRole('cell').nth(1)).toHaveText(firstNames[i - 1 + 15]);
    }

    // does not work correctly
    /*
      for (let i = 1; i < 15; i++) {
        const row1 = page.getByRole('row').nth(i);
        const row2 = page.getByRole('row').nth(i + 1);
        expect(
          await row1.evaluate(async () => ((await row1.textContent()) || '') < ((await row2.textContent()) || '')),
        ).toBeTruthy();
      }
      */
  });

  test('empty input', async () => {
    await page.getByRole('button', { name: 'Hinzufügen' }).click();
    await page.getByRole('button', { name: 'Speichern' }).click();
    await expect(page.getByText('Dieses Feld darf nicht leer').first()).toBeVisible();
    await page.getByText('Dieses Feld darf nicht leer').nth(1).click();
    await page.getByRole('button').first().click();
  });

  test('tags', async () => {
    await page.getByRole('link', { name: 'Tag' }).click();
    while (
      (await page.getByRole('cell', { name: 'Open menu' }).first().isVisible()) &&
      (await page.getByRole('row', { name: 'Name' }).getByRole('checkbox').isVisible())
    ) {
      await page.getByRole('row', { name: 'Name' }).getByRole('checkbox').check();
      await page.keyboard.press('Delete');
      await page.getByRole('button', { name: 'Löschen' }).click();
      await page.waitForTimeout(100);
    }

    await page.getByRole('button', { name: 'Hinzufügen' }).click();
    await page.getByRole('textbox').click();
    await page.getByRole('textbox').fill('ATag');
    await page.getByRole('button', { name: 'Speichern' }).click();

    await page.getByRole('button', { name: 'Hinzufügen' }).click();
    await page.getByRole('textbox').click();
    await page.getByRole('textbox').fill('BTag');
    await page.getByRole('button', { name: 'Speichern' }).click();

    await page.getByRole('link', { name: 'Schüler' }).click();
    await page.getByRole('row').nth(1).getByRole('button').click();
    await page.getByRole('menuitem', { name: 'Editieren' }).click();
    await page.getByRole('combobox').click();
    await page.getByRole('option', { name: 'ATag' }).click();
    await page.getByRole('option', { name: 'BTag' }).click();
    await page.getByRole('combobox').click();
    await page.getByRole('button', { name: 'Speichern' }).click();
    
    await expect(page.locator('tbody')).toContainText('ATag,BTag');
    await page.getByRole('row').nth(2).getByRole('button').click();
    await page.getByRole('menuitem', { name: 'Editieren' }).click();
    await page.getByRole('combobox').click();
    await page.getByRole('option', { name: 'BTag' }).click();
    await page.getByText('Vorname: Nachname: Tags: BTag').click();
    await page.getByRole('button', { name: 'Speichern' }).click();
    await expect(page.locator('tbody')).toContainText(/BTag/);
  })
});
