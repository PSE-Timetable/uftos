<script lang="ts">
  import { beforeNavigate, goto } from '$app/navigation';
  import * as AlertDialog from '$lib/elements/ui/alert-dialog';
  import Button from '$lib/elements/ui/button/button.svelte';
  import { getDefaultUcdlFile, getUcdlFile, setUcdlFile, validateUcdlFile } from '$lib/sdk/fetch-client';
  import { toast } from '$lib/utils/resources';
  import { debounce } from 'lodash-es';
  import type { editor } from 'monaco-editor';
  import { onMount } from 'svelte';

  let saveAlertOpen: boolean = false;
  let saveAlertMessage: string;
  let savedValue: string;
  let leavePageAlertOpen: boolean = false;
  let leavePageTo: URL;
  let uploadElement: HTMLDivElement;
  let downloadElement: HTMLAnchorElement;
  let element: HTMLDivElement;
  let ucdlEditor: editor.IStandaloneCodeEditor;
  let ucdl: string;

  const onUpload = async (file?: File | null) => {
    const text = (await file?.text()) || '';
    const lastLine = text.split('\n').length;

    ucdlEditor.executeEdits(null, [
      { text, range: { startColumn: 1, endColumn: 1, startLineNumber: 1, endLineNumber: lastLine } },
    ]);
    ucdlEditor.pushUndoStop();
  };

  const onDownload = () => {
    const blob = new File([ucdlEditor.getValue()], 'ucdl.yml', { type: 'text/x-yaml' });
    downloadElement.href = URL.createObjectURL(blob);
    downloadElement.click();
    URL.revokeObjectURL(downloadElement.href);
  };

  const onInsertTemplate = async () => {
    const template: string = await getDefaultUcdlFile();
    const lineCount = ucdlEditor.getModel()?.getLineCount() || 1;
    const lastLine = lineCount === 1 ? 1 : lineCount + 1;

    ucdlEditor.executeEdits(null, [
      {
        text: `${lastLine > 1 ? '\n' : ''}${template}`,
        range: {
          startColumn: 1,
          endColumn: 1,
          startLineNumber: lastLine,
          endLineNumber: lastLine + template.split('\n').length + 1,
        },
      },
    ]);
    ucdlEditor.pushUndoStop();
    ucdlEditor.revealLineInCenter(lastLine + 1);
  };

  const onValidate = async () => {
    const { success, message } = await validateUcdlFile({
      file: new Blob([ucdlEditor.getValue()]),
    });
    toast(success, message);
  };

  const onSave = async ({ value = ucdlEditor.getValue(), force = false }: { value?: string; force?: boolean }) => {
    const { success, message } = await setUcdlFile(
      {
        file: new Blob([value]),
      },
      { force },
    );
    if (success) {
      toast(success, message);
      ucdl = await getUcdlFile();
    } else {
      saveAlertMessage = message;
      savedValue = value;
      saveAlertOpen = true;
    }
  };

  beforeNavigate(({ cancel, to }) => {
    if (!to) {
      return;
    }

    if (to.url.searchParams.get('force')) {
      to.url.searchParams.delete('force');
      return;
    }

    if (ucdlEditor.getValue() !== ucdl) {
      leavePageAlertOpen = true;
      leavePageTo = to?.url;
      cancel();
    }
  });

  onMount(async () => {
    const monaco = await import('monaco-editor');
    ucdl = await getUcdlFile();

    monaco.editor.addKeybindingRule({
      keybinding: monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyP,
      command: 'editor.action.quickCommand',
    });

    ucdlEditor = monaco.editor.create(element, {
      value: await getUcdlFile(),
      language: 'yaml',
      automaticLayout: true,
    });

    ucdlEditor.onDidChangeModelContent(debounce(onValidate, 1500));

    ucdlEditor.addAction({
      id: 'save',
      label: 'Save File',
      keybindings: [monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyS],
      run: () => onSave({}),
    });

    ucdlEditor.addAction({
      id: 'upload',
      label: 'Upload File',
      keybindings: [monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyU],
      run: uploadElement.click,
    });

    ucdlEditor.addAction({
      id: 'download',
      label: 'Download File',
      keybindings: [monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyD],
      run: onDownload,
    });

    ucdlEditor.addAction({
      id: 'validate',
      label: 'Validate Code',
      run: onValidate,
    });

    ucdlEditor.addAction({
      id: 'insertTemplate',
      label: 'Insert Template',
      keybindings: [monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyI],
      run: onInsertTemplate,
    });
  });
</script>

<div class="flex w-full flex-col gap-4">
  <div class="flex justify-between mr-2 ml-2">
    <div class="flex gap-4">
      <Button class="hover:bg-accent text-white" on:click={() => onInsertTemplate()}>Template einfügen</Button>
      <Button class="hover:bg-accent text-white" on:click={() => onValidate()}>Code überprüfen</Button>
      <Button class="hover:bg-accent text-white" on:click={() => onSave({})}>Speichern</Button>
    </div>
    <div class="flex gap-4">
      <input
        bind:this={uploadElement}
        id="upload"
        type="file"
        class="hidden"
        accept="text/x-yaml,text/yaml,.yaml,.yml"
        on:input={(input) => onUpload(input.currentTarget.files?.item(0))}
      />
      <label for="upload">
        <Button
          class="hover:bg-accent text-white"
          on:click={() => {
            uploadElement.click();
          }}>Upload</Button
        >
      </label>
      <!-- svelte-ignore a11y-missing-attribute -->
      <a bind:this={downloadElement} download="ucdl.yml" target="_blank" class="hidden">Download the UCDL file </a>
      <Button class="hover:bg-accent text-white" on:click={onDownload}>Download</Button>
    </div>
  </div>
  <div bind:this={element} class="h-[800px]" />
</div>

<AlertDialog.Root bind:open={saveAlertOpen}>
  <AlertDialog.Trigger />
  <AlertDialog.Content>
    <AlertDialog.Header>
      <AlertDialog.Title>Datei konnte nicht gespeichert werden</AlertDialog.Title>
      <AlertDialog.Description>"{saveAlertMessage}" Trotzdem speichern?</AlertDialog.Description>
    </AlertDialog.Header>
    <AlertDialog.Footer>
      <AlertDialog.Cancel class="shadow-custom">Nein</AlertDialog.Cancel>
      <AlertDialog.Action on:click={() => onSave({ value: savedValue, force: true })} class="text-red-600 shadow-custom"
        >Ja</AlertDialog.Action
      >
    </AlertDialog.Footer>
  </AlertDialog.Content>
</AlertDialog.Root>

<AlertDialog.Root bind:open={leavePageAlertOpen}>
  <AlertDialog.Trigger />
  <AlertDialog.Content>
    <AlertDialog.Header>
      <AlertDialog.Title>Seite verlassen</AlertDialog.Title>
      <AlertDialog.Description>Du hast noch Änderungen. Sicher, dass du fortfahren möchtest?</AlertDialog.Description>
    </AlertDialog.Header>
    <AlertDialog.Footer>
      <AlertDialog.Cancel class="shadow-custom">Nein</AlertDialog.Cancel>
      <AlertDialog.Action
        on:click={async () => {
          const url = leavePageTo;
          url?.searchParams.append('force', 'true');
          await goto(url);
        }}
        class="text-red-600 shadow-custom">Ja</AlertDialog.Action
      >
    </AlertDialog.Footer>
  </AlertDialog.Content>
</AlertDialog.Root>
