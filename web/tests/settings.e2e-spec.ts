import { expect, test, type Page } from '@playwright/test';

let page: Page;

test.describe.configure({ mode: 'serial' });

test.describe('Settings', () => {
  test.beforeAll('delete all existing timeslots', async ({ browser }) => {
    page = await browser.newPage();
    await page.goto('http://localhost:5173/');
    await page.getByRole('link').first().click();
    await page.waitForURL('http://localhost:5173/admin');
    await expect(async () => {
      await page.getByRole('button', { name: 'Einstellungen' }).click();
      await page.getByRole('menuitem', { name: 'Allgemeine Einstell.' }).click({
        timeout: 1000,
      });
      await page.waitForURL('http://localhost:5173/admin/settings');
    }).toPass();
    await page.getByPlaceholder('Anzahl').click();
    await page.getByPlaceholder('Anzahl').fill('0');
    await expect(async () => {
      await expect(page.getByPlaceholder('Länge')).toBeEditable();
      await page.getByPlaceholder('Länge').click();
      await page.getByPlaceholder('Länge').fill('45');
      await expect(page.getByPlaceholder('Länge')).toHaveValue('45');
    }).toPass();
    await page.getByPlaceholder(':45').click();
    await page.getByPlaceholder(':45').fill('08:00');
    await expect(async () => {
      await expect(page.getByPlaceholder('Länge')).toBeEditable();
      await page.getByPlaceholder('Länge').click();
      await page.getByPlaceholder('Länge').fill('45');
      await expect(page.getByPlaceholder('Länge')).toHaveValue('45');
    }).toPass();
  });

  test('create timeslots and breaks', async () => {
    await page.getByPlaceholder('Anzahl').click();
    await page.getByPlaceholder('Anzahl').fill('8');
    await expect(page.locator('body')).toContainText('1. Stunde von 08:00 bis 08:45');
    await expect(page.locator('body')).toContainText('2. Stunde von 08:45 bis 09:30');
    await expect(page.locator('body')).toContainText('3. Stunde von 09:30 bis 10:15');
    await expect(page.locator('body')).toContainText('4. Stunde von 10:15 bis 11:00');
    await expect(page.locator('body')).toContainText('5. Stunde von 11:00 bis 11:45');
    await expect(page.locator('body')).toContainText('6. Stunde von 11:45 bis 12:30');
    await expect(page.locator('body')).toContainText('7. Stunde von 12:30 bis 13:15');
    await expect(page.locator('body')).toContainText('8. Stunde von 13:15 bis 14:00');

    await page.locator('div:nth-child(3) > button').click();
    await page.locator('div:nth-child(6) > button').click();
    await page.locator('div:nth-child(9) > button').click();
    await page.getByPlaceholder('Länge').nth(1).click();
    await page.getByPlaceholder('Länge').nth(1).fill('20');
    await page.getByPlaceholder('Länge').nth(2).click();
    await page.getByPlaceholder('Länge').nth(2).fill('10');
    await page.getByPlaceholder('Länge').nth(3).click();
    await page.getByPlaceholder('Länge').nth(3).fill('60');

    await expect(page.locator('div:nth-child(3) > button')).toBeDisabled();
    await expect(page.locator('div:nth-child(6) > button')).toBeDisabled();
    await expect(page.locator('div:nth-child(9) > button')).toBeDisabled();

    await expect(page.locator('body')).toContainText('1. Stunde von 08:00 bis 08:45');
    await expect(page.locator('body')).toContainText('2. Stunde von 08:45 bis 09:30');
    await expect(page.locator('body')).toContainText('Pause von 09:30 bis 09:50');
    await expect(page.locator('body')).toContainText('3. Stunde von 09:50 bis 10:35');
    await expect(page.locator('body')).toContainText('4. Stunde von 10:35 bis 11:20');
    await expect(page.locator('body')).toContainText('Pause von 11:20 bis 11:30');
    await expect(page.locator('body')).toContainText('5. Stunde von 11:30 bis 12:15');
    await expect(page.locator('body')).toContainText('6. Stunde von 12:15 bis 13:00');
    await expect(page.locator('body')).toContainText('Pause von 13:00 bis 14:00');
    await expect(page.locator('body')).toContainText('7. Stunde von 14:00 bis 14:45');
    await expect(page.locator('body')).toContainText('8. Stunde von 14:45 bis 15:30');
    await page.getByRole('button', { name: 'Speichern' }).click();
  });

  test('change timeslot length', async () => {
    await page.getByLabel('Timeslots Länge:').click();
    await page.getByLabel('Timeslots Länge:').fill('60');
    await expect(page.locator('body')).toContainText('1. Stunde von 08:00 bis 09:00');
    await expect(page.locator('body')).toContainText('2. Stunde von 09:00 bis 10:00');
    await expect(page.locator('body')).toContainText('Pause von 10:00 bis 10:20');
    await expect(page.locator('body')).toContainText('3. Stunde von 10:20 bis 11:20');
    await expect(page.locator('body')).toContainText('4. Stunde von 11:20 bis 12:20');
    await expect(page.locator('body')).toContainText('5. Stunde von 12:30 bis 13:30');
    await expect(page.locator('body')).toContainText('6. Stunde von 13:30 bis 14:30');
    await expect(page.locator('body')).toContainText('Pause von 14:30 bis 15:30');
    await expect(page.locator('body')).toContainText('7. Stunde von 15:30 bis 16:30');
    await expect(page.locator('body')).toContainText('8. Stunde von 16:30 bis 17:30');
    await page.getByRole('button', { name: 'Speichern' }).click();
  });

  test('change starting time', async () => {
    await page.getByPlaceholder(':45').click();
    await page.getByPlaceholder(':45').fill('07:52');
    await expect(page.locator('body')).toContainText('1. Stunde von 07:52 bis 08:52');
    await expect(page.locator('body')).toContainText('2. Stunde von 08:52 bis 09:52');
    await expect(page.locator('body')).toContainText('Pause von 09:52 bis 10:12');
    await expect(page.locator('body')).toContainText('3. Stunde von 10:12 bis 11:12');
    await expect(page.locator('body')).toContainText('4. Stunde von 11:12 bis 12:12');
    await expect(page.locator('body')).toContainText('Pause von 12:12 bis 12:22');
    await expect(page.locator('body')).toContainText('5. Stunde von 12:22 bis 13:22');
    await expect(page.locator('body')).toContainText('6. Stunde von 13:22 bis 14:22');
    await expect(page.locator('body')).toContainText('Pause von 14:22 bis 15:22');
    await expect(page.locator('body')).toContainText('7. Stunde von 15:22 bis 16:22');
    await expect(page.locator('body')).toContainText('8. Stunde von 16:22 bis 17:22');
    await page.getByRole('button', { name: 'Speichern' }).click();
  });

  test('timeslot persistance', async () => {
    await page.locator('.inline-flex').first().click();
    await page.getByRole('button', { name: 'Einstellungen' }).click();
    await page.getByRole('menuitem', { name: 'Allgemeine Einstell.' }).click();
    await expect(page.locator('body')).toContainText('1. Stunde von 07:52 bis 08:52');
    await expect(page.locator('body')).toContainText('2. Stunde von 08:52 bis 09:52');
    await expect(page.locator('body')).toContainText('Pause von 09:52 bis 10:12');
    await expect(page.locator('body')).toContainText('3. Stunde von 10:12 bis 11:12');
    await expect(page.locator('body')).toContainText('4. Stunde von 11:12 bis 12:12');
    await expect(page.locator('body')).toContainText('Pause von 12:12 bis 12:22');
    await expect(page.locator('body')).toContainText('5. Stunde von 12:22 bis 13:22');
    await expect(page.locator('body')).toContainText('6. Stunde von 13:22 bis 14:22');
    await expect(page.locator('body')).toContainText('Pause von 14:22 bis 15:22');
    await expect(page.locator('body')).toContainText('7. Stunde von 15:22 bis 16:22');
    await expect(page.locator('body')).toContainText('8. Stunde von 16:22 bis 17:22');
    await expect(page.getByPlaceholder('Anzahl')).toHaveValue('8');
    await expect(page.getByLabel('Timeslots Länge:')).toHaveValue('60');
    await expect(page.getByPlaceholder(':45')).toHaveValue('07:52');
  });

  test('delete break and timeslot', async () => {
    await page.locator('div:nth-child(10) > button').click();
    await page.locator('div:nth-child(7) > button').click();
    await expect(page.locator('body')).not.toContainText('Pause von 14:22 bis 15:22');
    await expect(page.locator('body')).not.toContainText('Pause von 12:12 bis 12:22');
    await page.getByPlaceholder('Anzahl').click();
    await page.getByPlaceholder('Anzahl').fill('1');

    await expect(page.locator('body')).toContainText('1. Stunde von 07:52 bis 08:52');
    await expect(page.locator('body')).not.toContainText('2. Stunde von 08:52 bis 09:52');
    await expect(page.locator('body')).not.toContainText('Pause von 09:52 bis 10:12');
    await expect(page.locator('body')).not.toContainText('3. Stunde von 10:12 bis 11:12');
    await expect(page.locator('body')).not.toContainText('4. Stunde von 11:12 bis 12:12');
    await expect(page.locator('body')).not.toContainText('5. Stunde von 12:22 bis 13:22');
    await expect(page.locator('body')).not.toContainText('6. Stunde von 13:22 bis 14:22');
    await expect(page.locator('body')).not.toContainText('7. Stunde von 15:22 bis 16:22');
    await expect(page.locator('body')).not.toContainText('8. Stunde von 16:22 bis 17:22');
    await page.getByRole('button', { name: 'Speichern' }).click();
  });
});
