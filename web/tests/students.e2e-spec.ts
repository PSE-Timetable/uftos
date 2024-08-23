import { expect, test } from '@playwright/test';

test.describe('Students page', () => {
  test('data table', async ({ page }) => {
    await page.goto('http://localhost:5173/');
    await page.getByRole('link').first().click();
    await page.locator('div:nth-child(2) > .bg-accent').first().click();
    await page.waitForTimeout(500); //not ideal, but helps to load the page fully

    //delete existing entries
    if (
      (await page.getByRole('cell', { name: 'Open menu' }).first().isVisible()) &&
      (await page.getByRole('row', { name: 'Vorname Nachname Tags' }).getByRole('checkbox').isVisible())
    ) {
      await page.getByRole('row', { name: 'Vorname Nachname Tags' }).getByRole('checkbox').check();
      await page.keyboard.press('Delete');
      await page.getByRole('button', { name: 'Löschen' }).click();
    }

    //create entities
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

    //check table
    await expect(page.getByRole('row')).toHaveCount(4); // header counts as row
    await expect(page.locator('tbody')).toContainText('a');
    await expect(page.locator('tbody')).toContainText('odgsogdpnpowad');
    await expect(page.locator('tbody')).toContainText('test1');
    await expect(page.locator('tbody')).toContainText('b');
    await expect(page.locator('tbody')).toContainText('pdhfgfönwßemü');
    await expect(page.locator('tbody')).toContainText('test35');

    //search (hard to test with fts and "random" values.)
    await page.getByPlaceholder('Suche...').click();
    await page.getByPlaceholder('Suche...').fill('b');
    await expect(page.getByRole('row')).toHaveCount(2);

    //edit entity
    await page.getByRole('row', { name: 'a b Open menu' }).getByRole('button').click();
    await page.getByRole('menuitem', { name: 'Editieren' }).click();
    await expect(page.getByRole('textbox').first()).toHaveValue('a');
    await expect(page.getByRole('textbox').nth(1)).toHaveValue('b');
    await page.getByRole('textbox').first().click();
    await page.getByRole('textbox').first().fill('atsfws');
    await page.getByRole('button', { name: 'Speichern' }).click();
    await expect(page.locator('tbody')).toContainText('atsfws');

    //check selection
    await expect(page.locator('body')).toContainText('0 von 3 Zeile(n) ausgewählt.');
    await page.getByRole('row', { name: 'test1 test35 Open menu' }).getByRole('checkbox').check();
    await expect(page.locator('body')).toContainText('1 von 3 Zeile(n) ausgewählt.');
    await page.getByRole('row', { name: 'test1 test35 Open menu' }).getByRole('button').click();
    await page.getByRole('menuitem', { name: 'Löschen' }).click();
    await expect(page.getByRole('row')).toHaveCount(3);
    await expect(page.locator('body')).toContainText('1 von 2 Zeile(n) ausgewählt.');
    await page.keyboard.press('Delete');
    await page.getByRole('button', { name: 'Löschen' }).click();
    await expect(page.getByRole('row')).toHaveCount(2); //should only work after merge of PR #360
    await expect(page.locator('body')).toContainText('0 von 1 Zeile(n) ausgewählt.');
  });
});
