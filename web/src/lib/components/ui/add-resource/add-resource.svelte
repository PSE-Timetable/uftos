<script lang="ts">
  import { Checkbox } from '$lib/elements/ui/checkbox';
  import * as Form from '$lib/elements/ui/form';
  import { Input } from '$lib/elements/ui/input';
  import { getGrades, type GradeResponseDto, type Subject, type Tag } from '$lib/sdk/fetch-client';
  import TagsMultipleSelect from '$lib/components/ui/tags-multiple-select/tags-multiple-select.svelte';
  import ComboBox from '$lib/elements/ui/combo-box/combo-box.svelte';
  import { error } from '@sveltejs/kit';
  import SuperDebug, { superForm } from 'sveltekit-superforms';
  import { zodClient } from 'sveltekit-superforms/adapters';
  import { goto } from '$app/navigation';

  export let descriptions: string[];
  export let data;
  export let schema;
  export let update: (formData: Record<string, string | number | string[]>, subjectIds?: string[]) => Promise<void>;
  export let tags: Tag[] | undefined = undefined;
  export let subjects: Subject[] | undefined = undefined;
  export let entitySubjectsIds: Set<string> = new Set();
  export let grades: GradeResponseDto[] | undefined = undefined;

  let selectedSubjects = (subjects || []).map((subject) => ({
    id: subject.id,
    selected: entitySubjectsIds.has(subject.id),
  }));

  let gradesAvailable: boolean = (grades || []).length > 0;

  let form = superForm(data, {
    validators: zodClient(schema),
    SPA: true,
  });
  let { form: formData, enhance, validateForm } = form;
  let names = Object.keys($formData).slice(1);

  async function updateGrades(value: string) {
    try {
      grades = await getGrades({ sort: ['name,asc'] }, { search: value });
    } catch {
      error(400, { message: 'Could not fetch grades' });
    }
  }

  async function handleSubmit() {
    let validatedForm = await validateForm();
    if (!validatedForm.valid) {
      return;
    }
    await update(
      $formData,
      selectedSubjects.filter((subject) => subject.selected).map((subject) => subject.id),
    );
    await goto('./');
  }
</script>

<form method="POST" use:enhance>
  <div class="grid grid-cols-[max-content,1fr] gap-8 p-4">
    {#each descriptions as description, i}
      <div class="text-lg font-bold flex">{description}</div>
      <div class="flex flex-col gap-1 w-80">
        <Form.Field {form} name={names[i]}>
          <Form.Control let:attrs>
            <Input
              {...attrs}
              bind:value={$formData[names[i]]}
              background={true}
              class="rounded-none font-normal flex max-w-80"
            />
          </Form.Control>
          <Form.FieldErrors />
        </Form.Field>
      </div>
    {/each}

    {#if names.includes('capacity')}
      <div class="text-lg font-bold flex">Kapazität:</div>
      <div class="flex flex-col gap-1 w-80">
        <Form.Field {form} name="capacity">
          <Form.Control let:attrs>
            <Input
              {...attrs}
              type="number"
              min="0"
              bind:value={$formData.capacity}
              background={true}
              class="rounded-none font-normal flex max-w-80"
            />
          </Form.Control>
          <Form.FieldErrors />
        </Form.Field>
      </div>
    {/if}

    {#if grades}
      <div class="flex text-lg font-bold">Stufe:</div>
      {#if gradesAvailable}
        <div class="flex flex-col">
          <Form.Field {form} name="grades">
            <Form.Control>
              <ComboBox
                onSearch={(value) => updateGrades(value)}
                data={grades.map((grade) => ({ value: grade.id, label: grade.name }))}
                bind:selectedId={$formData.grades[0]}
                shadow={true}
              />
            </Form.Control>
            <Form.FieldErrors />
          </Form.Field>
        </div>
      {:else}
        <div class="text-lg font-semibold text-red-600">Es müssen Grades vorhanden sein.</div>
      {/if}
    {/if}

    {#if subjects}
      <div class="flex text-lg font-bold">Fächer:</div>
      {#if subjects.length > 0}
        <div class="flex flex-wrap bg-white rounded-md gap-2 p-4 shadow-custom max-w-80">
          {#each subjects as subject, i}
            <div class="flex items-top space-x-2">
              <Checkbox class="m-1" bind:checked={selectedSubjects[i].selected} />
              <p class="break-all w-full">
                {subject.name}
              </p>
            </div>
          {/each}
        </div>
      {:else}
        <div class="text-lg font-semibold">Keine Fächer vorhanden.</div>
      {/if}
    {/if}

    {#if tags}
      <div class=" flex text-lg font-bold">Tags:</div>
      {#if tags.length > 0}
        <div class="w-80 flex">
          <Form.Field {form} name="tags">
            <Form.Control>
              <TagsMultipleSelect {tags} bind:selectedTagIds={$formData.tags} />
            </Form.Control>
          </Form.Field>
        </div>
      {:else}
        <div class="text-lg font-semibold">Keine Tags vorhanden.</div>
      {/if}
    {/if}

    <Form.Button
      on:click={() => handleSubmit()}
      class="col-start-2 p-8 text-lg w-80 bg-accent text-white hover:bg-accent flex"
      variant="secondary"
      disabled={grades && !gradesAvailable}
    >
      Speichern
    </Form.Button>
  </div>
  <SuperDebug data={$formData}></SuperDebug>
</form>
