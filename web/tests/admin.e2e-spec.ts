import { expect, test } from '@playwright/test';

test.describe('Admin Overview', () => {
  test('shows statistics', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('link').first().click();
    await expect(page).toHaveURL('/admin');

    await page.locator('div.gap-4:nth-child(1) > p:nth-child(1)').waitFor();
    await expect(page.getByText('0', { exact: true }).count()).resolves.toBe(5);
  });
});
