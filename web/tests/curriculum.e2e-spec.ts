import {
  createGrade,
  createSubject,
  deleteGrades,
  deleteStudentGroup,
  deleteSubjects,
  getGrades,
  getStudentGroups,
  getSubjects,
} from '$lib/sdk/fetch-client';
import { expect, test, type Locator, type Page } from '@playwright/test';

let page: Page;
let countLocator: Locator;

test.describe.configure({ mode: 'serial' });

test.describe('Curriculum', () => {
  test.beforeAll('Delete existing entities', async ({ browser }) => {
    page = await browser.newPage();
    const subjects = await getSubjects({});
    if (subjects.length > 0) {
      await deleteSubjects(subjects.map((subject) => subject.id));
    }
    const groups = await getStudentGroups({ page: 0 }).then(({ content }) => content ?? []);
    if (groups.length > 0) {
      for (const group of groups) {
        await deleteStudentGroup(group.id);
      }
    }
    const grades = await getGrades({});
    if (grades.length > 0) {
      await deleteGrades(grades.map((grade) => grade.id));
    }

    for (let i = 0; i < 5; i++) {
      await createSubject({ name: `subject${i}`, tagIds: [] });
    }
    for (let i = 0; i < 5; i++) {
      await createGrade({ name: `grade${i}`, tagIds: [], studentGroupIds: [] }).then(({ id }) => id);
    }
    await page.goto('/');
    await page.getByRole('link').first().click();
    await page.getByRole('link', { name: 'Curriculum' }).click();
  });

  test('set curriculum', async () => {
    countLocator = page.getByLabel('lessonCount');
    await expect(countLocator).toHaveCount(5);
    await countLocator.first().getByRole('button').nth(1).click({ clickCount: 6 });
    await countLocator.nth(2).getByRole('button').nth(1).click({ clickCount: 9 });
    await countLocator.nth(4).getByRole('button').nth(1).click({ clickCount: 4 });
    await expect(countLocator.first()).toContainText(' 6');
    await expect(countLocator.nth(1)).toContainText(' 0');
    await expect(countLocator.nth(2)).toContainText(' 9');
    await expect(countLocator.nth(3)).toContainText(' 0');
    await expect(countLocator.nth(4)).toContainText(' 4');
    await page.getByText('Speichern').click();
    await page.getByRole('combobox').click();
    await page.getByRole('option', { name: 'grade1' }).first().click();
    await expect(countLocator.first()).toContainText(' 0');
    await expect(countLocator.nth(1)).toContainText(' 0');
    await expect(countLocator.nth(2)).toContainText(' 0');
    await expect(countLocator.nth(3)).toContainText(' 0');
    await expect(countLocator.nth(4)).toContainText(' 0');
    await countLocator.first().getByRole('button').nth(1).click({ clickCount: 8 });
    await countLocator.nth(1).getByRole('button').nth(1).click({ clickCount: 2 });
    await countLocator.nth(3).getByRole('button').nth(1).click({ clickCount: 5 });
    await expect(countLocator.first()).toContainText(' 8');
    await expect(countLocator.nth(1)).toContainText(' 2');
    await expect(countLocator.nth(2)).toContainText(' 0');
    await expect(countLocator.nth(3)).toContainText(' 5');
    await expect(countLocator.nth(4)).toContainText(' 0');
    await page.getByText('Speichern').click();
  });

  test('switch grades', async () => {
    countLocator = page.getByLabel('lessonCount');
    await page.getByRole('combobox').click();
    await page.getByRole('option', { name: 'grade4' }).first().click();
    await expect(countLocator.first()).toContainText('0');
    await expect(countLocator.nth(1)).toContainText(' 0');
    await expect(countLocator.nth(2)).toContainText(' 0');
    await expect(countLocator.nth(3)).toContainText(' 0');
    await expect(countLocator.nth(4)).toContainText(' 0');
    await page.getByRole('combobox').click();
    await page.getByRole('option', { name: 'grade0' }).first().click();
    await expect(countLocator.first()).toContainText(' 6');
    await expect(countLocator.nth(1)).toContainText(' 0');
    await expect(countLocator.nth(2)).toContainText(' 9');
    await expect(countLocator.nth(3)).toContainText(' 0');
    await expect(countLocator.nth(4)).toContainText(' 4');
    await page.getByRole('combobox').click();
    await page.getByRole('option', { name: 'grade1' }).first().click();
    await expect(countLocator.first()).toContainText(' 8');
    await expect(countLocator.nth(1)).toContainText(' 2');
    await expect(countLocator.nth(2)).toContainText(' 0');
    await expect(countLocator.nth(3)).toContainText(' 5');
    await expect(countLocator.nth(4)).toContainText(' 0');
  });

  test('check non-negative', async () => {
    countLocator = page.getByLabel('lessonCount');
    await countLocator.first().getByRole('button').first().click({ clickCount: 12 });
    await countLocator.nth(1).getByRole('button').first().click({ clickCount: 5 });
    await countLocator.nth(2).getByRole('button').first().click({ clickCount: 9 });
    await countLocator.nth(3).getByRole('button').first().click({ clickCount: 7 });
    await countLocator.nth(4).getByRole('button').first().click({ clickCount: 4 });
    await expect(countLocator.first()).toContainText(' 0');
    await expect(countLocator.nth(1)).toContainText(' 0');
    await expect(countLocator.nth(2)).toContainText(' 0');
    await expect(countLocator.nth(3)).toContainText(' 0');
    await expect(countLocator.nth(4)).toContainText(' 0');
  });

  test('add subjects', async () => {
    for (let i = 0; i < 5; i++) {
      await createSubject({ name: `subject1${i}`, tagIds: [] });
    }
    await page.reload();
    await expect(countLocator).toHaveCount(10);
    await expect(countLocator.nth(2)).toContainText(' 0');
    await expect(countLocator.nth(3)).toContainText(' 0');
    await expect(countLocator.nth(4)).toContainText(' 0');
    await expect(countLocator.nth(5)).toContainText(' 0');
    await expect(countLocator.nth(6)).toContainText(' 0');
  });

  test('remove subjects', async () => {
    const subjects = await getSubjects({ sort: ['name,asc'] });
    await deleteSubjects(subjects.slice(-3).map((subject) => subject.id));
    await page.reload();
    await expect(countLocator).toHaveCount(7);
    await expect(countLocator.first()).toContainText(' 6');
    await expect(countLocator.nth(1)).toContainText(' 0');
    await expect(countLocator.nth(2)).toContainText(' 0');
    await expect(countLocator.nth(3)).toContainText(' 0');
    await expect(countLocator.nth(4)).toContainText(' 0');
    await expect(countLocator.nth(5)).toContainText(' 0');
    await expect(countLocator.nth(6)).toContainText(' 0');

    const subjects2 = await getSubjects({ sort: ['name,asc'] });
    await deleteSubjects(subjects2.map((subject) => subject.id));
    await page.reload();
    await expect(countLocator).toHaveCount(0);
    await expect(page.locator('body')).toContainText('Keine Fächer vorhanden.');
    await expect(page.getByText('Speichern')).toBeDisabled();
    const grades = await getGrades({});
    await deleteGrades(grades.map((grade) => grade.id));
    await page.reload();
    await expect(page.locator('body')).toContainText('Keine Stufen vorhanden.');
    await expect(page.getByText('Speichern')).toBeDisabled();
  });
});
