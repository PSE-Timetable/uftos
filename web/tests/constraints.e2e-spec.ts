import { createStudent, deleteStudents, getStudents } from '$lib/sdk/fetch-client';
import { expect, test, type Page } from '@playwright/test';

let page: Page;

test.describe('Constraints', () => {
  test.beforeAll('Delete existing constraints', async ({ browser }) => {
    page = await browser.newPage();
    await page.goto('/');
    await page.getByRole('link').first().click();
    await page.getByRole('link', { name: 'UCDL Editor' }).click();
    await expect(async () => {
      const fileChooserPromise = page.waitForEvent('filechooser', { timeout: 500 });
      await page.getByText('Upload').click();
      const fileChooser = await fileChooserPromise;
      await fileChooser.setFiles('./tests/empty-ucdl.yml');
    }).toPass();
    const students = await getStudents({ page: 0 }).then(({ content }) => content ?? []);
    if (students.length > 0) {
      await deleteStudents(students.map((student) => student.id));
    }
    for (let i = 0; i < 5; i++) {
      await createStudent({ firstName: `Con${i}`, lastName: `straint${i}`, tagIds: [] });
    }
  });

  test('create constraints', async () => {
    await page.getByRole('button', { name: 'Template einfügen' }).click();
    await page.getByRole('button', { name: 'Speichern' }).click();
    if (await page.getByRole('heading', { name: 'Datei konnte nicht' }).isVisible()) {
      await page.getByRole('button', { name: 'Ja' }).click();
    }
    await page.locator('.inline-flex').first().click();
    while (await page.getByRole('heading', { name: 'Seite verlassen' }).isVisible()) {
      await page.getByRole('button', { name: 'Nein' }).click();
      await page.getByRole('button', { name: 'Speichern' }).click();
      await page.locator('.inline-flex').first().click();
    }
    await page.getByRole('link', { name: 'Constraints' }).click();
    await page
      .locator('div')
      .filter({ hasText: 'Schüler {student} kann nicht' })
      .nth(4)
      .getByText('Ressource auswählen')
      .click();
    await page.getByRole('option', { name: 'Con0 straint0' }).click();
    await page.getByText('Hinzufügen').first().click();
    for (let i = 1; i < 5; i++) {
      await page
        .locator('button')
        .filter({ hasText: `Con${i - 1} straint${i - 1}` })
        .click();
      await page.getByRole('option', { name: `Con${i} straint${i}` }).click();
      await page.getByText('Hinzufügen').first().click();
    }
    for (let i = 0; i < 5; i++) {
      await expect(page.locator('tbody').first()).toContainText(`Con${i} straint${i}`);
    }
  });

  test('delete constraint instances', async () => {
    await page.getByRole('button', { name: 'Open menu' }).click();
    await page.getByRole('menuitem', { name: 'Löschen' }).click();
    await page.getByRole('row', { name: 'student' }).getByRole('checkbox').check();
    await page.getByRole('button', { name: 'Löschen' }).click();
    await expect(page.locator('tbody').first()).toBeHidden();
  });

  test('delete signatures', async () => {
    await page.locator('button').filter({ hasText: `Con4 straint4` }).click();
    await page.getByRole('option', { name: `Con0 straint0` }).click();
    await page.getByText('Hinzufügen').first().click();
    await page.getByRole('link', { name: 'UCDL Editor' }).click();
    const fileChooserPromise = page.waitForEvent('filechooser');
    await page.getByText('Upload').click();
    const fileChooser = await fileChooserPromise;
    await fileChooser.setFiles('./empty-ucdl.yml');
    await page.getByRole('button', { name: 'Speichern' }).click();
    await expect(page.getByRole('heading', { name: 'Datei konnte nicht' })).toBeVisible();
    await page.getByRole('button', { name: 'Ja' }).click();
    await page.getByRole('button', { name: 'Template einfügen' }).click();
    await page.getByRole('button', { name: 'Speichern' }).click();
    const fileChooserPromise1 = page.waitForEvent('filechooser');
    await page.getByText('Upload').click();
    const fileChooser1 = await fileChooserPromise1;
    await fileChooser1.setFiles('./empty-ucdl.yml');
    await page.getByRole('button', { name: 'Speichern' }).click();
    await page.locator('.inline-flex').first().click();
    await expect(page).toHaveURL('/admin');
  });
});
