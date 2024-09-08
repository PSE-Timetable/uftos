import { createTag, defaults, type TagRequestDto } from '$lib/sdk/fetch-client';
import { expect, test, type Page } from '@playwright/test';

let page: Page;

test.describe.configure({ mode: 'serial' });

//tests need to be done in order or they might break!
test.describe('tags page', () => {
  test.beforeAll('delete all existing tags', async ({ browser }) => {
    defaults.baseUrl = 'http://localhost:5173/api';
    page = await browser.newPage();
    await page.goto('/');
    await page.getByRole('link').first().click();
    await page.getByRole('link', { name: 'Tag' }).click();
    await expect(page).toHaveURL('/admin/tags');
    await page.getByRole('row').first().waitFor();
    await page.getByRole('row', { name: 'Name' }).getByRole('checkbox').waitFor();
    await expect(async () => {
      while (await page.locator('tbody').isVisible()) {
        await page.getByRole('row', { name: 'Name' }).getByRole('checkbox').check();
        await page.keyboard.press('Delete');
        await page.getByRole('button', { name: 'Löschen' }).click({ timeout: 250 });
        await expect(page.getByText('Zeile(n) ausgewählt')).toContainText('0 von');
      }
    }).toPass();
  });

  test('create entities', async () => {
    await expect(page.locator('body')).toContainText('Hinzufügen');
    await expect(async () => {
      await page.getByRole('button', { name: 'Hinzufügen' }).click();
      await expect(page).toHaveURL('/admin/tags/new', { timeout: 750 });
    }).toPass();
    await page.getByRole('textbox').first().click();
    await page.getByRole('textbox').first().fill('odgsogdpnpowad');

    await page.getByRole('button', { name: 'Speichern' }).click();
    await expect(page.getByRole('row').nth(1)).toContainText('odgsogdpnpowad');
    await page.getByRole('button', { name: 'Hinzufügen' }).click();
    await page.getByRole('textbox').first().click();
    await page.getByRole('textbox').first().fill('test1');
    await page.getByRole('button', { name: 'Speichern' }).click();
    await page.getByRole('button', { name: 'Hinzufügen' }).click();
    await page.getByRole('textbox').first().click();
    await page.getByRole('textbox').first().fill('abc');
    await page.getByRole('button', { name: 'Speichern' }).click();
    await page.getByRole('button', { name: 'Name' }).click();
  });

  test('check table', async () => {
    await expect(page.getByRole('row')).toHaveCount(4); // header counts as row
    await expect(page.getByRole('row').nth(1)).toContainText('abc');
    await expect(page.getByRole('row').nth(2)).toContainText('odgsogdpnpowad');
    await expect(page.getByRole('row').nth(3)).toContainText('test1');
  });

  test('search', async () => {
    await page.getByPlaceholder('Suche...').click();
    await page.getByPlaceholder('Suche...').fill('abc');
    await expect(page.getByRole('row')).toHaveCount(2);
    await expect(page.getByRole('row').nth(1)).toContainText('a');
    await page.getByPlaceholder('Suche...').click();
    await page.getByPlaceholder('Suche...').fill('odgsogdpnpowad');
    await expect(page.getByRole('row')).toHaveCount(2);
    await expect(page.getByRole('row').nth(1)).toContainText('odgsogdpnpowad');
    await page.getByPlaceholder('Suche...').fill('');
    await expect(page.getByRole('row')).toHaveCount(4);
  });

  test('edit entity', async () => {
    await page.getByRole('row', { name: 'abc' }).getByRole('button').click();
    await page.getByRole('menuitem', { name: 'Editieren' }).click();
    await expect(page.getByRole('textbox').first()).toHaveValue('abc');
    await page.getByRole('textbox').first().click();
    await page.getByRole('textbox').first().fill('atsfws');
    await page.getByRole('button', { name: 'Speichern' }).click();
    await expect(page.locator('tbody')).toContainText('atsfws');
  });

  test('select and delete', async () => {
    await expect(page.locator('body')).toContainText('0 von 3 Zeile(n) ausgewählt.');
    await page.getByRole('row', { name: 'test1 Open menu' }).getByRole('checkbox').check();
    await expect(page.locator('body')).toContainText('1 von 3 Zeile(n) ausgewählt.');
    await page.getByRole('row', { name: 'odgsogdpnpowad Open menu' }).getByRole('button').click();
    await page.getByRole('menuitem', { name: 'Löschen' }).click();
    await expect(page.getByRole('row')).toHaveCount(3);
    await expect(page.locator('body')).toContainText('1 von 2 Zeile(n) ausgewählt.');
    await page.keyboard.press('Delete');
    await page.getByRole('button', { name: 'Löschen' }).click();
    await expect(page.getByRole('row')).toHaveCount(2);
    await expect(page.locator('body')).toContainText('0 von 1 Zeile(n) ausgewählt.');
    await page.getByRole('row', { name: 'Name' }).getByRole('checkbox').check();
    await page.keyboard.press('Delete');
    await page.getByRole('button', { name: 'Löschen' }).click();
  });

  test('pagination', async () => {
    for (let i = 0; i < 25; i++) {
      const requestDto: TagRequestDto = {
        tagName: `tag${i}`,
      };
      await createTag(requestDto);
    }
    await page.reload();
    await expect(page.getByRole('row')).toHaveCount(16);
    await expect(page.locator('body')).toContainText('0 von 25 Zeile(n) ausgewählt.');
    await expect(page.getByLabel('Previous')).toBeDisabled();
    await expect(page.getByLabel('Page 1')).toHaveCSS('border-color', 'rgb(43, 109, 136)');
    await expect(page.getByLabel('Page 2')).not.toHaveCSS('border-color', 'rgb(43, 109, 136)');
    await expect(page.getByLabel('Next')).toBeEnabled();
    await expect(async () => {
      await page.getByLabel('Next').click();
      await expect(page.getByRole('row')).toHaveCount(11, { timeout: 250 });
    }).toPass();
    await expect(page.getByLabel('Previous')).toBeEnabled();
    await expect(page.getByLabel('Next')).toBeDisabled();
    await expect(page.getByLabel('Page 1')).not.toHaveCSS('border-color', 'rgb(43, 109, 136)');
    await expect(page.getByLabel('Page 2')).toHaveCSS('border-color', 'rgb(43, 109, 136)');
    await page.getByPlaceholder('Suche...').fill('tag1');
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
    await page.getByRole('button', { name: 'Name' }).click();
    const names = [];
    for (let i = 0; i < 25; i++) {
      names.push(`tag${i}`);
    }
    names.sort();

    for (let i = 1; i <= 15; i++) {
      await expect(page.getByRole('row').nth(i).getByRole('cell').nth(1)).toHaveText(names[i - 1]);
    }

    await page.getByLabel('Next').click();
    for (let i = 1; i <= 10; i++) {
      await expect(page.getByRole('row').nth(i).getByRole('cell').nth(1)).toHaveText(names[i - 1 + 15]);
    }
  });

  test('empty input', async () => {
    await page.getByRole('button', { name: 'Hinzufügen' }).click();
    await page.getByRole('button', { name: 'Speichern' }).click();
    await expect(page.getByText('Der Name darf nicht leer sein.')).toBeVisible();
    await page.getByRole('button').first().click();
  });
});
