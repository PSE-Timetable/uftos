import {
  createGrade,
  createStudent,
  createSubject,
  createTag,
  deleteGrades,
  deleteStudents,
  deleteSubjects,
  deleteTags,
  getGrades,
  getStudents,
  getSubjects,
  getTags,
  type GradeRequestDto,
  type StudentRequestDto,
  type SubjectRequestDto,
  type TagRequestDto,
} from '$lib/sdk/fetch-client';
import { expect, test, type Page } from '@playwright/test';

let page: Page;

test.describe.configure({ mode: 'serial' });

test.describe('Student group page', () => {
  test.beforeAll('delete existing groups', async ({ browser }) => {
    page = await browser.newPage();
    await page.goto('/');
    await page.getByRole('link').first().click();
    await page.getByRole('link', { name: 'Gruppe' }).click();
    await expect(page).toHaveURL('/admin/studentGroups');
    while (await page.getByRole('button', { name: 'Schüler hinzufügen' }).first().isVisible()) {
      try {
        await page.locator('.flex > .flex > button:nth-child(2)').first().click({ timeout: 500 });
      } catch {
        // this timeout is fine, can happen sometimes
      }
    }
    const totalElements = await getStudents({ page: 0 }).then(({ totalElements }) => totalElements);
    const students = await getStudents({ page: 0, size: totalElements }).then(({ content }) => content ?? []);
    if (students.length > 0) {
      await deleteStudents(students.map((student) => student.id));
    }
    const grades = await getGrades({});
    if (grades.length > 0) {
      await deleteGrades(grades.map((grade) => grade.id));
    }
    const tags = await getTags({});
    if (tags.length > 0) {
      await deleteTags(tags.map((tag) => tag.id));
    }
    const subjects = await getSubjects({});
    if (tags.length > 0) {
      await deleteSubjects(subjects.map((subject) => subject.id));
    }

    for (let i = 0; i < 25; i++) {
      const requestDto: StudentRequestDto = {
        firstName: `Max${i}`,
        lastName: `Mustermann${i}`,
        tagIds: [],
      };
      await createStudent(requestDto);
    }
  });

  test('empty create', async () => {
    await page.getByRole('button', { name: 'Hinzufügen' }).click();
    await expect(page.locator('body')).toContainText('Es müssen Grades vorhanden sein.');
    await expect(page.locator('body')).toContainText('Keine Fächer vorhanden.');
    await expect(page.locator('body')).toContainText('Keine Tags vorhanden.');
    await expect(page.getByText('Speichern')).toBeDisabled();
    await page.getByRole('button').first().click();
  });

  test('create', async () => {
    for (let i = 0; i < 5; i++) {
      const subjectRequestDto: SubjectRequestDto = { name: `subject${i}`, tagIds: [] };
      await createSubject(subjectRequestDto);
      const tagRequestDto: TagRequestDto = { tagName: `tag${i}` };
      await createTag(tagRequestDto);
      const gradeRequestDto: GradeRequestDto = { name: `grade${i}`, studentGroupIds: [], tagIds: [] };
      await createGrade(gradeRequestDto);
    }
    await page.getByRole('button', { name: 'Hinzufügen' }).click();
    await page.getByRole('textbox').click();
    await page.getByRole('textbox').fill('Group1');
    await page.locator('button').filter({ hasText: 'Ressource auswählen' }).click();
    await page.getByRole('option', { name: 'grade2' }).click();
    await page.getByRole('checkbox').nth(3).click();
    await page.getByRole('checkbox').nth(1).click();
    await page.getByRole('checkbox').nth(4).click();
    await page.getByRole('combobox').nth(1).click();
    await page.getByRole('option', { name: 'tag1' }).click();
    await page.getByRole('option', { name: 'tag2' }).click();
    await page.getByRole('combobox').nth(1).click();
    await page.getByText('Speichern').click();
    await expect(page.getByText('Keine Gruppen vorhanden.')).toBeHidden();
    await expect(page.getByText('Group1')).toBeVisible();
    await page.locator('.flex > div > .flex > button').first().click();
    await expect(page.locator('body')).toContainText('grade2');
    await expect(page.getByRole('checkbox').nth(3)).toBeChecked();
    await expect(page.getByRole('checkbox').nth(1)).toBeChecked();
    await expect(page.getByRole('checkbox').nth(4)).toBeChecked();
    const locator1 = page.getByText('tag1, tag2');
    const locator2 = page.getByText('tag2, tag1');
    await expect(locator1.or(locator2)).toBeVisible();
    await page.getByText('Speichern').click();
  });

  test('create no selected', async () => {
    await page.getByRole('button', { name: 'Hinzufügen', exact: true }).click();
    await page.getByRole('button', { name: 'Speichern' }).click();
    await expect(page.locator('body')).toContainText('Der Name darf nicht leer sein.');
    await expect(page.locator('body')).toContainText('Es muss eine Stufe ausgewählt werden.');
    await page.getByRole('button').first().click();
  });

  test('add students to group', async () => {
    await expect(async () => {
      await page.getByRole('combobox').first().click();
      await page.getByRole('option', { name: 'Max2 Mustermann2' }).click({ timeout: 500 });
      await page.getByRole('button', { name: 'Schüler hinzufügen' }).first().click();
    }).toPass();
    await page.locator('button').filter({ hasText: 'Max2 Mustermann2' }).click();
    await page.getByRole('option', { name: 'Max9 Mustermann9' }).click();
    await page.getByRole('button', { name: 'Schüler hinzufügen' }).first().click();
    await page.locator('button').filter({ hasText: 'Max9 Mustermann9' }).click();
    await page.getByRole('option', { name: 'Max15 Mustermann15' }).click();
    await page.getByRole('button', { name: 'Schüler hinzufügen' }).first().click();
    await page.locator('button').filter({ hasText: 'Max15 Mustermann15' }).click();
    await page.getByRole('option', { name: 'Max5 Mustermann5' }).click();
    await page.getByRole('button', { name: 'Schüler hinzufügen' }).first().click();
    await page.locator('button').filter({ hasText: 'Max5 Mustermann5' }).click();
    await page.getByRole('option', { name: 'Max1 Mustermann1' }).click();
    await page.getByRole('button', { name: 'Schüler hinzufügen' }).first().click();
    await expect(page.locator('tbody')).toContainText('Max1 Mustermann1');
    await expect(page.locator('tbody')).toContainText('Max2 Mustermann2');
    await expect(page.locator('tbody')).toContainText('Max15 Mustermann15');
    await expect(page.locator('tbody')).toContainText('Max5 Mustermann5');
    await expect(page.locator('tbody')).toContainText('Max9 Mustermann9');
  });

  test('search', async () => {
    await page.getByPlaceholder('Suche...').first().click();
    await page.getByPlaceholder('Suche...').first().fill('Max1');
    await expect(page.locator('tbody').first().getByRole('row').first()).toContainText('Max1 Mustermann1');
    await expect(page.locator('tbody').first().getByRole('row')).toHaveCount(1);
    await page.getByPlaceholder('Suche...').first().fill('');
  });
});
