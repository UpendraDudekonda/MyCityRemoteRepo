# 🚀 Collaborator Guide: Forking, Cloning, and Managing Git in Eclipse

This guide explains how to **fork**, **clone**, **commit**, **push**, and **pull** changes using **Eclipse** for effective collaboration.

---

## 📌 1. Forking the Repository
Before working on the project, you need to **fork** the main repository to create your own copy.

### **Steps to Fork:**
1. **Go to GitHub** and navigate to the repository you want to fork.
2. Click on the **Fork** button (top right corner).
3. GitHub will create a copy of the repository under **your account**.

✅ Now you have your own repository where you can work without affecting the original repo.

---

## 📌 2. Cloning the Forked Repository in Eclipse
Once the fork is created, you need to clone it into Eclipse.

### **Steps to Clone:**
1. **Open Eclipse** and go to **Window > Show View > Other**.
2. Search for **Git Repositories** and open it.
3. In the **Git Repositories** view, click **Clone a Git Repository**.
4. Enter the **GitHub Repository URL** (your forked repo URL).
5. Click **Next**, and **Finish** after Eclipse fetches the repo.

✅ Now the repository is downloaded into Eclipse, and you can start working on it.

---

## 📌 3. Creating a New Branch (Recommended Best Practice)
Before making changes, always work on a new branch.

### **Steps to Create a Branch:**
1. Open **Git Perspective** (Window > Perspective > Open Perspective > Other > Git).
2. In the **Git Repositories** tab, right-click your repository and select **Switch To > New Branch**.
3. Enter a branch name (e.g., `feature-new-update`).
4. Click **Finish** to create and switch to the new branch.

✅ Now you're working on a separate branch to keep your main branch clean.

---

## 📌 4. Making Changes and Committing in Eclipse
Once you’ve made your changes, you need to **commit** them.

### **Steps to Commit:**
1. **Modify the code** inside your Eclipse project.
2. Go to **Git Staging** (Window > Show View > Other > Git Staging).
3. In **Unstaged Changes**, select the files you modified and click **Add to Staged Changes**.
4. Enter a meaningful commit message (e.g., `Fixed login bug in WishlistService`).
5. Click **Commit and Push** to push changes to your GitHub fork.

✅ Your changes are now committed and available in your GitHub fork.

---

## 📌 5. Pushing Changes to Your Forked Repository
If you committed locally but didn’t push, follow these steps:

### **Steps to Push:**
1. In Eclipse, right-click the repository in **Git Repositories**.
2. Click **Push to Upstream** (or `Push`).
3. Select the correct branch and click **Next**, then **Finish**.

✅ Your changes are now pushed to GitHub.

---

## 📌 6. Pulling the Latest Changes from the Main Repository
You must keep your fork **up to date** with the original repository.

### **Steps to Pull Updates from the Main Repo:**
1. Go to your **forked repository on GitHub**.
2. Click **Pull Requests > New Pull Request**.
3. Select `base repository: main-repo` → `compare: your-fork`.
4. Click **Create Pull Request** and **Merge** when ready.

✅ Now your fork is updated with the latest changes from the main repo.

---

## 📌 7. Updating Your Local Eclipse Repo with the Latest Changes
After updating the fork, you need to pull those changes into Eclipse.

### **Steps to Pull Updates into Eclipse:**
1. Right-click on the **repository** in Eclipse Git View.
2. Click **Pull** to fetch the latest updates.
3. Merge any conflicts if required.

✅ Now your local Eclipse project is synced with the latest code.

---

## 📌 8. Submitting Your Changes to the Main Repository (Pull Request)
When your changes are complete, submit a **Pull Request (PR)** to merge them into the main repository.

### **Steps to Submit a PR:**
1. Push your changes to **your fork** (`git push origin your-branch`).
2. Open **GitHub** and go to your forked repository.
3. Click **Compare & pull request**.
4. Select the **main repository** as the base and your branch as the compare.
5. Add a description and click **Create Pull Request**.

✅ Once approved, your changes will be merged into the main project.

---

## 📌 9. Additional Notes
- Always **sync your fork** with the main repository before starting a new feature.
- Use **meaningful commit messages** to make collaboration easier.
- If facing conflicts, resolve them manually in **Git Staging**.

---

## 🖼️ Visual Guide (Images & Screenshots)
For detailed screenshots, you can refer to the following:
- **Forking a repo in GitHub**
- **Cloning a repo in Eclipse**
- **Committing & pushing changes in Eclipse**
- **Creating a Pull Request in GitHub**

> If needed, contributors can generate screenshots for each step.

🚀 **Happy Coding!**

