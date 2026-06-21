# GitHub Setup

Git has been installed as a portable Git for Windows distribution at:

```text
C:\Users\zengzeng\Documents\Codex\2026-06-21\w\tools\git
```

The Git executable is:

```text
C:\Users\zengzeng\Documents\Codex\2026-06-21\w\tools\git\cmd\git.exe
```

The project repository has been initialized on branch `main`.

The GitHub remote has been configured:

```powershell
git remote -v
```

```text
origin  https://github.com/pwzlyx/yunqiapp.git (fetch)
origin  https://github.com/pwzlyx/yunqiapp.git (push)
```

Before the first commit, configure your Git identity:

```powershell
git config --global user.name "YOUR_NAME"
git config --global user.email "YOUR_EMAIL"
```

Then run these commands from this folder:

```powershell
git add .
git commit -m "Initialize Android app workspace"
```

If you already have a GitHub repository, connect it:

```powershell
git remote add origin https://github.com/YOUR_ACCOUNT/YOUR_REPO.git
git push -u origin main
```

If you install GitHub CLI and sign in, you can create the repository from this folder:

```powershell
gh auth login
gh repo create YOUR_REPO --private --source=. --remote=origin --push
```
