import { createStudent, defaults, type StudentRequestDto } from '$lib/sdk/fetch-client';
import { expect, test, type Page } from '@playwright/test';

let page: Page;

test.describe.configure({ mode: 'serial' });

//tests need to be done in order or they might break!
test.describe('Students page', () => {
  test.beforeAll('delete all existing students', async ({ browser }) => {
    defaults.baseUrl = 'http://localhost:5173/api';
    page = await browser.newPage();
    await page.goto('/');
    await page.getByRole('link').first().click();
    await page.locator('div:nth-child(2) > .bg-accent').first().click();
    await expect(page).toHaveURL('/admin/students');
    await page.getByRole('row').first().waitFor();
    await page.getByRole('row', { name: 'Vorname Nachname Tags' }).getByRole('checkbox').waitFor();
    await expect(async () => {
      while (await page.locator('tbody').isVisible()) {
        await page.getByRole('row', { name: 'Vorname Nachname Tags' }).getByRole('checkbox').check();
        await page.keyboard.press('Delete');
        await page.getByRole('button', { name: 'Löschen' }).click({ timeout: 250 });
        await expect(page.getByText('Zeile(n) ausgewählt')).toContainText('0 von');
      }
    }).toPass();
  });

  test('create entities', async () => {
    await expect(page.locator('body')).toContainText('Hinzufügen');
    await page.getByRole('button', { name: 'Hinzufügen' }).click();
    await expect(page).toHaveURL('/admin/students/new');
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
    await expect(page.getByRole('row').nth(1)).toContainText('a');
    await expect(page.getByRole('row').nth(1)).toContainText('b');
    await expect(page.getByRole('row').nth(2)).toContainText('odgsogdpnpowad');
    await expect(page.getByRole('row').nth(2)).toContainText('pdhfgfönwßemü');
    await expect(page.getByRole('row').nth(3)).toContainText('test1');
    await expect(page.getByRole('row').nth(3)).toContainText('test35');
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
      const requestDto: StudentRequestDto = {
        firstName: `Max${i}`,
        lastName: `Mustermann${i}`,
        tagIds: [],
      };
      await createStudent(requestDto);
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
  });

  test('empty input', async () => {
    await page.getByRole('button', { name: 'Hinzufügen' }).click();
    await page.getByRole('button', { name: 'Speichern' }).click();
    await expect(page.getByText('Der Vorname darf nicht leer sein.')).toBeVisible();
    await expect(page.getByText('Der Nachname darf nicht leer sein.')).toBeVisible();
    await page.getByRole('button').first().click();
  });

  test('tags', async () => {
    await page.getByRole('link', { name: 'Tag' }).click();
    await expect(async () => {
      while (
        (await page.getByRole('cell', { name: 'Open menu' }).first().isVisible()) &&
        (await page.getByRole('row', { name: 'Name' }).getByRole('checkbox').isVisible())
      ) {
        await page.getByRole('row', { name: 'Name' }).getByRole('checkbox').check();
        await page.keyboard.press('Delete');
        await page.getByRole('button', { name: 'Löschen' }).click({
          timeout: 250,
        });
      }
    }).toPass();

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

    const locator1 = page.getByText('ATag,BTag');
    const locator2 = page.getByText('BTag,ATag');
    await expect(locator1.or(locator2)).toBeVisible();
    await page.getByRole('row').nth(2).getByRole('button').click();
    await page.getByRole('menuitem', { name: 'Editieren' }).click();
    await page.getByRole('combobox').click();
    await page.getByRole('option', { name: 'BTag' }).click();
    await page.getByText('Vorname: Nachname: Tags: BTag').click();
    await page.getByRole('button', { name: 'Speichern' }).click();
    await expect(page.getByRole('row').nth(2)).toContainText('BTag');
    await page.getByRole('row').nth(1).getByRole('button').click();
    await page.getByRole('menuitem', { name: 'Editieren' }).click();
    const locator3 = page.getByText('ATag,BTag');
    const locator4 = page.getByText('BTag,ATag');
    await expect(locator3.or(locator4)).toBeVisible();
    await page.getByRole('combobox').click();
    await expect(page.getByRole('option', { name: 'BTag' }).getByRole('img')).toBeVisible();
    await page.getByRole('option', { name: 'BTag' }).click();
    await page.getByRole('combobox').click();
    await page.getByRole('button', { name: 'Speichern' }).click();
    await expect(page.getByRole('row').nth(1)).toContainText('ATag');
  });
});
