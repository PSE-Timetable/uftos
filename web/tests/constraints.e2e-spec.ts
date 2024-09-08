import {
  createGrade,
  createStudent,
  createStudentGroup,
  createSubject,
  createTag,
  createTeacher,
  defaults,
  deleteGrades,
  deleteStudentGroup,
  deleteStudents,
  deleteSubjects,
  deleteTags,
  deleteTeachers,
  getGrades,
  getStudentGroups,
  getStudents,
  getSubjects,
  getTags,
  getTeachers,
} from '$lib/sdk/fetch-client';
import { expect, test, type Page } from '@playwright/test';

let page: Page;

test.describe.configure({ mode: 'serial' });

test.describe('Constraints', () => {
  test.beforeAll('Delete existing constraints', async ({ browser }) => {
    defaults.baseUrl = 'http://localhost:5173/api';
    page = await browser.newPage();
    await page.goto('/');
    await page.getByRole('link').first().click();
    await page.getByRole('link', { name: 'UCDL Editor' }).click();
    await expect(async () => {
      const fileChooserPromise = page.waitForEvent('filechooser', { timeout: 1000 });
      await page.getByText('Upload').click();
      const fileChooser = await fileChooserPromise;
      await fileChooser.setFiles('./tests/empty-ucdl.yml');
      await expect(page.locator('.view-line').first()).toBeEmpty({ timeout: 750 });
    }).toPass();
    const totalStudents = await getStudents({ page: 0 }).then(({ totalElements }) => totalElements);
    const students = await getStudents({ page: 0, size: totalStudents }).then(({ content }) => content ?? []);
    if (students.length > 0) {
      await deleteStudents(students.map((student) => student.id));
    }
    const tags = await getTags({});
    if (tags.length > 0) {
      await deleteTags(tags.map((tag) => tag.id));
    }
    const subjects = await getSubjects({});
    if (tags.length > 0) {
      await deleteSubjects(subjects.map((subject) => subject.id));
    }
    const totalTeachers = await getTeachers({ page: 0 }).then(({ totalElements }) => totalElements);
    const teachers = await getTeachers({ page: 0, size: totalTeachers }).then(({ content }) => content ?? []);
    if (teachers.length > 0) {
      await deleteTeachers(teachers.map((teacher) => teacher.id));
    }
    const totalGroups = await getStudentGroups({ page: 0 }).then(({ totalElements }) => totalElements);
    const groups = await getStudentGroups({ page: 0, size: totalGroups }).then(({ content }) => content ?? []);
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
      await createStudent({ firstName: `Con${i}`, lastName: `straint${i}`, tagIds: [] });
    }
    for (let i = 0; i < 5; i++) {
      await createTag({ tagName: `tag${i}` });
    }
    await createSubject({ name: `subject0`, tagIds: [] });
    const gradeId = await createGrade({ name: 'grade', tagIds: [], studentGroupIds: [] }).then(({ id }) => id);
    for (let i = 0; i < 5; i++) {
      await createStudentGroup({ name: `group${i}`, gradeIds: [gradeId], studentIds: [], subjectIds: [], tagIds: [] });
    }
    for (let i = 0; i < 5; i++) {
      await createTeacher({ firstName: `teach${i}`, lastName: `er${i}`, acronym: `${i}`, subjectIds: [], tagIds: [] });
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
      await page.getByRole('button', { name: 'Nein' }).first().click();
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

    //limit set of rooms for a subject
    const locator = page.getByText('unterrichtet werden. Subject');
    await expect(locator).toBeVisible();
    await locator.getByText('Ressource auswählen').first().click();
    await page.getByRole('option', { name: 'subject0' }).click();
    await locator.getByText('Ressource auswählen').first().click();
    await page.getByRole('option', { name: 'tag0' }).click();
    await locator.getByText('Hinzufügen').click();
    await expect(page.locator('table').nth(6)).toContainText('subject0 tag0');

    //teacher teaches group in subject
    const locator1 = page.getByText('in Fach {subject}. teacher');
    await expect(locator1).toBeVisible();
    await locator1.getByText('Ressource auswählen').first().click();
    await page.getByRole('option', { name: 'teach0 er0' }).click();
    await locator1.getByText('Ressource auswählen').first().click();
    await page.getByRole('option', { name: 'group0' }).click();
    await locator1.getByText('Ressource auswählen').first().click();
    await page.getByRole('option', { name: 'subject0' }).click();
    await page.getByText('in Fach subject0. teacher').getByText('Hinzufügen').click();
    await expect(page.locator('table').nth(1)).toContainText('teach0 er0 group0 subject0');
  });

  test('delete constraint instances', async () => {
    await page.getByRole('button', { name: 'Open menu' }).first().click();
    await page.getByRole('menuitem', { name: 'Löschen' }).click();
    await page.getByRole('row', { name: 'student' }).getByRole('checkbox').check();
    await page.keyboard.press('Delete');
    await page.getByRole('button', { name: 'Löschen' }).click();
    await expect(page.locator('tbody').first()).toBeHidden();
  });

  test('delete signatures', async () => {
    await page.locator('button').filter({ hasText: `Con4 straint4` }).click();
    await page.getByRole('option', { name: `Con0 straint0` }).click();
    await page.getByText('Hinzufügen').first().click();
    await page.getByRole('link', { name: 'UCDL Editor' }).click();
    await expect(async () => {
      const fileChooserPromise = page.waitForEvent('filechooser', { timeout: 1000 });
      await page.getByText('Upload').click();
      const fileChooser = await fileChooserPromise;
      await fileChooser.setFiles('./tests/empty-ucdl.yml');
    }).toPass();
    await page.getByRole('button', { name: 'Speichern' }).click();
    await expect(page.getByRole('heading', { name: 'Datei konnte nicht' })).toBeVisible();
    await page.getByRole('button', { name: 'Ja' }).click();
    await page.getByRole('button', { name: 'Template einfügen' }).click();
    await page.getByRole('button', { name: 'Speichern' }).click();
    await expect(async () => {
      if (await page.getByRole('button', { name: 'Nein' }).isVisible()) {
        await page.getByRole('button', { name: 'Nein' }).click({ timeout: 1000 });
      }
      await page.locator('.inline-flex').first().click({ timeout: 1000 });
      await expect(page).toHaveURL('/admin', { timeout: 1250 });
    }).toPass();
  });
});
