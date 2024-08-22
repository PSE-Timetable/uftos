<script lang="ts">
  import { beforeNavigate } from '$app/navigation';
  import Button from '$lib/elements/ui/button/button.svelte';
  import { getDefaultUcdlFile, getUcdlFile, setUcdlFile, validateUcdlFile } from '$lib/sdk/fetch-client';
  import { debounce } from 'lodash-es';
  import type { editor } from 'monaco-editor';
  import { onMount } from 'svelte';
  import { toast as _toast } from 'svelte-sonner';

  let uploadElement: HTMLDivElement;
  let downloadElement: HTMLAnchorElement;
  let element: HTMLDivElement;
  let ucdlEditor: editor.IStandaloneCodeEditor;
  let ucdl: string;

  const toast = (success: boolean, message: string) =>
    success ? _toast.success(message) : _toast.error(message, { important: true, duration: 4000 });

  const onUpload = async (file?: File | null) => {
    await onSave(await file?.text());
  };

  const onDownload = () => {
    const blob = new File([ucdlEditor.getValue()], 'ucdl.yml', { type: 'text/x-yaml' });
    downloadElement.href = URL.createObjectURL(blob);
    downloadElement.click();
    URL.revokeObjectURL(downloadElement.href);
  };

  const onInsertTemplate = async () => {
    const template: string = await getDefaultUcdlFile();
    const lastLine = ucdlEditor.getModel()?.getLineCount() || 0;

    ucdlEditor.setValue(`${ucdlEditor.getValue()}\n\n${template}`);
    ucdlEditor.revealLineInCenter(lastLine + 2);
  };

  const onValidate = async () => {
    const { success, message } = await validateUcdlFile({
      file: new Blob([ucdlEditor.getValue()]),
    });
    toast(success, message);
  };

  const onSave = async (value: string = ucdlEditor.getValue()) => {
    const { success, message } = await setUcdlFile({
      file: new Blob([value]),
    });
    toast(success, message);
  };

  beforeNavigate(({ cancel }) => {
    if (ucdlEditor.getValue() !== ucdl && !confirm('Du hast noch Änderungen. Sicher, dass du fortfahren möchtest?')) {
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
      run: () => onSave(),
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
  });
</script>

<div class="flex w-full flex-col gap-4">
  <div class="flex justify-between mr-2 ml-2">
    <div class="flex gap-4">
      <Button class="hover:bg-accent text-white" on:click={() => onInsertTemplate()}>Template einfügen</Button>
      <Button class="hover:bg-accent text-white" on:click={() => onValidate()}>Code überprüfen</Button>
      <Button class="hover:bg-accent text-white" on:click={() => onSave()}>Speichern</Button>
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
